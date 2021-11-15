package com.koldyr.library.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.Library
import com.koldyr.library.controllers.TestDbInitializer.dbInitialized
import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Genre
import com.koldyr.library.model.Reader
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import org.hamcrest.Matchers.matchesRegex
import org.junit.Before
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import javax.sql.DataSource

object TestDbInitializer {
    @JvmStatic
    var dbInitialized: Boolean = false
}

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Library::class])
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

    @Before
    fun login() {
        if (!dbInitialized) {
            insertSupervisor()
            dbInitialized = true
        }
    }

    private fun insertSupervisor() {
        val template = JdbcTemplate(dataSource)

        ClassLoader.getSystemResourceAsStream("initial-data.sql").use {
            val sqlCommand = StringBuilder()
            val reader = BufferedReader(InputStreamReader(it!!))
            reader.lines().forEach { line ->
                if (line == null || line.startsWith("--")) {
                    //do nothing
                } else {
                    sqlCommand.append(line).append(' ')

                    if (line.endsWith(";")) {
                        template.execute(sqlCommand.toString())
                        logger.info(sqlCommand.toString())

                        sqlCommand.clear()
                    }
                }
            }
        }
    }

    protected fun createAuthor(): AuthorDTO {
        val index = RandomUtils.nextInt(0, 100_000)
        val year = RandomUtils.nextInt(1900, 2000)
        val author = AuthorDTO(null, "a${index}_name", "a${index}_last", LocalDate.of(year, 1, 1))

        val authorHeader = rest.post("/api/library/authors") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(author)
            with(httpBasic(userName, password))
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
            with(httpBasic(userName, password))
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<AuthorDTO>>()
        return mapper.readValue(response, typeRef)
    }

    protected fun createReader(): Reader {
        val index = RandomUtils.nextInt(0, 100_000)
        val reader = Reader()
        reader.firstName = "r${index}_fname"
        reader.lastName = "r${index}_lname"
        reader.mail = "r${index}_mail"
        reader.address = "r${index}_address"
        reader.phoneNumber = "r${index}_phone"
        reader.note = "r${index}_note"
        reader.password = RandomStringUtils.randomAscii(10)

        val readerHeader = rest.post("/api/library/readers") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(reader)
            with(httpBasic(userName, password))
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

    protected fun findAllReaders(): List<Reader> {
        val response = rest.get("/api/library/readers") {
            with(httpBasic(userName, password))
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<Reader>>()
        return mapper.readValue(response, typeRef)
    }

    protected fun getCurrentUser(): Reader {
        val response = rest.get("/api/library/readers/me") {
            with(httpBasic(userName, password))
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        return mapper.readValue(response, Reader::class.java)
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
            content = mapper.writeValueAsString(book)
            with(httpBasic(userName, password))
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
            with(httpBasic(userName, password))
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
            content = mapper.writeValueAsString(order)
            with(httpBasic(userName, password))
        }
                .andExpect {
                    status { isCreated() }
                }
                .andReturn().response.contentAsString

        val dto = mapper.readValue(response, OrderDTO::class.java)
        dto.ordered = null
        return dto
    }

    protected fun getOrdersForReader(reader: Reader): List<OrderDTO> {
        val response = rest.get("/api/library/readers/${reader.id}/orders") {
            with(httpBasic(userName, password))
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<OrderDTO>>()
        val orders = mapper.readValue(response, typeRef)
        orders.forEach { it.ordered = null }

        return orders
    }

    protected fun createFeedBack(book: BookDTO, reader: Reader) {
        val rate = RandomUtils.nextInt(0, 10)
        val feedback = FeedbackDTO(null, reader.id, book.id, LocalDateTime.now(), "feedback of reader '${reader.id}' for book '${book.id}'", rate)

        rest.post("/api/library/books/feedback") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(feedback)
            with(httpBasic(userName, password))
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { string(LOCATION, matchesRegex("/api/library/books/[\\d]+/feedbacks")) }
                }
    }
}
