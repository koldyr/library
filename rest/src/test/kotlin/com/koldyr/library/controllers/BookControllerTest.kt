package com.koldyr.library.controllers

import java.time.LocalDate
import java.time.LocalDateTime
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.Library
import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.SearchCriteria
import com.koldyr.library.model.Genre
import com.koldyr.library.model.Reader
import org.hamcrest.Matchers.matchesRegex
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Description of class BookControllerTest
 * @created: 2021-10-23
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Library::class])
@AutoConfigureMockMvc
@TestPropertySource(properties = ["spring.config.location = classpath:application-test.yaml"])
@IfProfileValue(name = "spring.profiles.active", values = ["int-test"])
class BookControllerTest {

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var rest: MockMvc

    @Test
    fun books() {
        val author = createAuthor()
        val book = createBook(author, 1)
        assertNotNull(book.id)

        var bookFromServer = readBook(book.id!!)
        assertEquals(book, bookFromServer)

        book.title = "New title for book ${book.id}"
        book.bookCover = "New bookCover for book ${book.id}"
        book.publishingHouse = "New publishingHouse for book ${book.id}"
        book.note = "New note for book ${book.id}"

        updateBook(book)

        bookFromServer = readBook(book.id!!)
        assertEquals(book, bookFromServer)

        assertBooks(book)

        deleteBook(book.id!!)

        rest.get("/api/library/books/${book.id}")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun feedbacks() {
        val author = createAuthor()
        val book = createBook(author, 2)

        createFeedBack(book, "f1_feedback", 5)
        createFeedBack(book, "f2_feedback", 9)

        val response = rest.get("/api/library/books/${book.id}/feedbacks") {
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
            }.andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<Array<FeedbackDTO>>()
        val feedbacks = mapper.readValue(response, typeRef)

        assertEquals(2, feedbacks.size)
    }

    @Test
    fun searchBooks() {
        val author = createAuthor()
        createBook(author, 3)
        createBook(author, 4)
        createBook(author, 5)
        createBook(author, 6)
        createBook(author, 7)
        createBook(author, 8)

        val searchCriteria = SearchCriteria()
        searchCriteria.title = "title"
        
        val books = searchBooks(searchCriteria)

        assertTrue { books.isNotEmpty() }

    }

    private fun createBook(author: AuthorDTO, index: Int): BookDTO {
        val book = BookDTO(null, "title $index", author.id, Genre.values()[index], "house $index", LocalDate.now(), "cover $index", "note $index", 5)

        val location: String? = rest.post("/api/library/books") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
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

    private fun createAuthor(): AuthorDTO {
        val author = AuthorDTO(null, "a1_name", "a1_last", LocalDate.of(1970, 10, 10))
        val authorHeader = rest.post("/api/library/authors") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
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

    private fun createFeedBack(book: BookDTO, value: String, rate: Int) {
        val reader = createReader()
        val feedback = FeedbackDTO(null, reader.id, book.id, LocalDateTime.now(), value, rate)

        rest.post("/api/library/books/feedback") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(feedback)
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                header { string(LOCATION, matchesRegex("/api/library/books/[\\d]+/feedbacks")) }
            }
    }

    private fun createReader(): Reader {
        val reader = Reader()
        reader.address = "r1_address"
        reader.firstName = "r1_fname"
        reader.lastName = "r1_lname"
        reader.mail = "r1_email"
        reader.note = "r1_note"
        reader.phoneNumber = "r1_phone"

        val location: String? = rest.post("/api/library/readers") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(reader)
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                header { string(LOCATION, matchesRegex("/api/library/readers/[\\d]+")) }
            }
            .andReturn().response.getHeader(LOCATION)

        val regex = Regex("/api/library/readers/(\\d+)")
        val matchResult = regex.find(location!!)
        reader.id = matchResult?.groups?.get(1)?.value?.toInt()
        return reader
    }

    private fun readBook(bookId: Int): BookDTO {
        val body: String = rest.get("/api/library/books/$bookId") {
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
            }.andReturn().response.contentAsString

        return mapper.readValue(body, BookDTO::class.java)
    }

    private fun updateBook(book: BookDTO) {
        rest.put("/api/library/books/${book.id}") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(book)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
            }
    }


    private fun deleteBook(bookId: Int) {
        rest.delete("/api/library/books/$bookId")
            .andExpect {
                status { isNoContent() }
            }
    }

    private fun assertBooks(book: BookDTO) {
        val response = rest.get("/api/library/books") {
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
            }
            .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<BookDTO>>()
        val books = mapper.readValue(response, typeRef)

        val fromServer = books.filter { dto -> dto.id == book.id }[0]
        assertEquals(book, fromServer)
    }

    private fun searchBooks(searchCriteria: SearchCriteria): List<BookDTO> {
        val response = rest.post("/api/library/books/search") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(searchCriteria)
        }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
            }
            .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<BookDTO>>()
        return mapper.readValue(response, typeRef)
    }
}