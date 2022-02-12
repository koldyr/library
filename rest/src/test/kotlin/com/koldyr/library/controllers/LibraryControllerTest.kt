package com.koldyr.library.controllers

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ClassLoader.getSystemResourceAsStream
import java.time.LocalDate
import java.time.LocalDateTime
import javax.sql.DataSource
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.controllers.TestDbInitializer.dbInitialized
import com.koldyr.library.controllers.TestDbInitializer.token
import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.CredentialsDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.ReaderDTO
import com.koldyr.library.model.Genre
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import org.h2.tools.RunScript
import org.hamcrest.Matchers.matchesRegex
import org.junit.jupiter.api.BeforeEach
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

object TestDbInitializer {
    @JvmStatic
    var dbInitialized: Boolean = false

    @JvmStatic
    var token: String? = null
}

@SpringBootTest
@AutoConfigureMockMvc
@IfProfileValue(name = "spring.profiles.active", values = ["int-test"])
abstract class LibraryControllerTest {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var rest: MockMvc

    @Value("\${library.test.user}")
    protected val userName: String = ""

    @Value("\${library.test.password}")
    protected val password: String = ""

    @BeforeEach
    fun setUp() {
        if (!dbInitialized) {
            insertSupervisor()
            dbInitialized = true
        }

        if (token == null) {
            token = login(CredentialsDTO(userName, password))
        }
    }

    private fun insertSupervisor() {
        getSystemResourceAsStream("initial-data.sql").use { stream: InputStream? ->
            BufferedReader(InputStreamReader(stream!!)).use { reader: BufferedReader ->
                RunScript.execute(dataSource.connection, reader)
                logger.info("DB initialized")
            }
        }
    }

    protected fun login(credentials: CredentialsDTO): String {
        return rest.post("/api/library/login") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(credentials)
        }
            .andExpect { status { isOk() } }
            .andReturn().response.getHeader(AUTHORIZATION)!!
    }

    protected fun createAuthor(): AuthorDTO {
        val index = RandomUtils.nextInt(0, 100_000)
        val year = RandomUtils.nextInt(1900, 2000)
        val author = AuthorDTO(null, "a${index}_name", "a${index}_last", LocalDate.of(year, 1, 1))

        val authorHeader = rest.post("/api/library/authors") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(author)
        }
                .andExpect {
                    status { isCreated() }
                }
                .andReturn().response.getHeader(LOCATION)

        val regex = Regex("/api/library/authors/(\\d+)")
        val matchResult = regex.find(authorHeader!!)
        author.id = matchResult?.groups?.get(1)?.value?.toInt()

        return author
    }

    protected fun findAllAuthors(): List<AuthorDTO> {
        val response = rest.get("/api/library/authors") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<AuthorDTO>>()
        return mapper.readValue(response, typeRef)
    }

    protected fun createReader(): ReaderDTO {
        val index = RandomUtils.nextInt(0, 100_000)
        val reader = ReaderDTO()
        reader.firstName = "r${index}_fname"
        reader.lastName = "r${index}_lname"
        reader.mail = "r${index}_mail"
        reader.address = "r${index}_address"
        reader.phoneNumber = "r${index}_phone"
        reader.note = "r${index}_note"
        reader.password = RandomStringUtils.randomAscii(10)

        val readerHeader = rest.post("/api/library/registration") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(reader)
        }
                .andExpect {
                    status { isCreated() }
                    header { string(LOCATION, matchesRegex("/api/library/readers/[\\d]+")) }
                }
                .andReturn().response.getHeader(LOCATION)

        val regex = Regex("/api/library/readers/(\\d+)")
        val matchResult = regex.find(readerHeader!!)
        reader.id = matchResult?.groups?.get(1)?.value?.toInt()

        return reader
    }

    protected fun findAllReaders(): List<ReaderDTO> {
        val response = rest.get("/api/library/readers") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<ReaderDTO>>()
        return mapper.readValue(response, typeRef)
    }

    protected fun getCurrentUser(): ReaderDTO {
        val response = rest.get("/api/library/readers/me") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        return mapper.readValue(response, ReaderDTO::class.java)
    }

    protected fun createBook(author: AuthorDTO): BookDTO {
        val index = RandomUtils.nextInt(0, 100_000)
        val count = RandomUtils.nextInt(1, 10)
        val genre = RandomUtils.nextInt(0, Genre.values().size - 1)
        val year = RandomUtils.nextInt(1900, 2021)

        val publicationDate = LocalDate.of(year, 1, 1)
        val book = BookDTO(null, "title $index", author.id, Genre.values()[genre], "house $index", publicationDate, "cover $index", "note $index", count)

        val location: String? = rest.post("/api/library/books") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(book)
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { string(LOCATION, matchesRegex("/api/library/books/[\\d]+")) }
                }
                .andReturn().response.getHeader(LOCATION)

        val regex = Regex("/api/library/books/(\\d+)")
        val matchResult = regex.find(location!!)
        book.id = matchResult?.groups?.get(1)?.value?.toInt()
        return book
    }

    protected fun findAllBooks(): List<BookDTO> {
        val response = rest.get("/api/library/books") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<BookDTO>>()
        return mapper.readValue(response, typeRef)
    }

    protected fun createOrder(bookId: Int, readerId: Int): OrderDTO {
        val order = OrderDTO()
        order.bookId = bookId
        order.readerId = readerId
        order.notes = "reader '$readerId' took '$bookId' book"

        val response = rest.post("/api/library/books/take") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(order)
        }
                .andExpect {
                    status { isCreated() }
                }
                .andReturn().response.contentAsString

        val dto = mapper.readValue(response, OrderDTO::class.java)
        dto.ordered = null
        return dto
    }

    protected fun getOrdersForReader(reader: ReaderDTO): List<OrderDTO> {
        val response = rest.get("/api/library/readers/${reader.id}/orders") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<OrderDTO>>()
        val orders = mapper.readValue(response, typeRef)
        orders.forEach { it.ordered = null }

        return orders
    }

    protected fun createFeedBack(book: BookDTO, reader: ReaderDTO) {
        val rate = RandomUtils.nextInt(0, 10)
        val feedback = FeedbackDTO(null, reader.id, book.id, LocalDateTime.now(), "feedback of reader '${reader.id}' for book '${book.id}'", rate)

        rest.post("/api/library/books/feedback") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(feedback)
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { string(LOCATION, matchesRegex("/api/library/books/[\\d]+/feedbacks")) }
                }
    }
}
