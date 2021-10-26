package com.koldyr.library.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.Library
import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Authority
import com.koldyr.library.model.Genre
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.AuthorityRepository
import com.koldyr.library.services.ReaderService
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils
import org.apache.commons.lang3.StringUtils
import org.hamcrest.Matchers.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.*
import org.springframework.http.MediaType.*
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Library::class])
@AutoConfigureMockMvc
@TestPropertySource(properties = ["spring.config.location = classpath:application-test.yaml"])
@IfProfileValue(name = "spring.profiles.active", values = ["int-test"])
abstract class LibraryControllerTest {

    companion object {
        @JvmStatic var basicHash: String = ""
    }

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var rest: MockMvc

    @Autowired
    lateinit var readerService: ReaderService

    @Autowired
    lateinit var authorityRepository: AuthorityRepository

    @PostConstruct
    fun onInit() {
        if (StringUtils.isEmpty(basicHash)) {
            val password = RandomStringUtils.randomAscii(10)
            val reader = Reader()
            reader.firstName = "koldyr"
            reader.lastName = "koldyr"
            reader.mail = "me@koldyr.com"
            reader.phoneNumber = "+375297709965"
            reader.address = "Minsk"
            reader.password = password
            reader.authorities.add(Authority(0, "reader"))
            readerService.create(reader)

            basicHash = Base64.getEncoder().encodeToString(("${reader.mail}:${password}").toByteArray())
        }
    }

    protected fun createAuthor(): AuthorDTO {
        val index = RandomUtils.nextInt(0, 100_000)
        val year = RandomUtils.nextInt(1900, 2000)
        val author = AuthorDTO(null, "a${index}_name", "a${index}_last", LocalDate.of(year, 1, 1))

        val authorHeader = rest.post("/api/library/authors") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            headers { setBasicAuth(basicHash) }
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
            headers { setBasicAuth(basicHash) }
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
            headers { setBasicAuth(basicHash) }
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

    protected fun findAllReaders(): List<Reader> {
        val response = rest.get("/api/library/readers") {
            headers { setBasicAuth(basicHash) }
        }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<Reader>>()
        return mapper.readValue(response, typeRef)
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
            headers { setBasicAuth(basicHash) }
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
            headers { setBasicAuth(basicHash) }
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
            headers { setBasicAuth(basicHash) }
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

    protected fun getOrdersForReader(reader: Reader): List<OrderDTO> {
        val response = rest.get("/api/library/readers/${reader.id}/orders") {
            headers { setBasicAuth(basicHash) }
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
            headers { setBasicAuth(basicHash) }
            content = mapper.writeValueAsString(feedback)
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { string(LOCATION, matchesRegex("/api/library/books/[\\d]+/feedbacks")) }
                }
    }


}
