package com.koldyr.library.controllers

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.PageResultDTO
import com.koldyr.library.dto.SearchCriteria
import org.junit.Test
import org.springframework.http.MediaType.*
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
class BookControllerTest: LibraryControllerTest() {

    @Test
    fun books() {
        val author = createAuthor()
        val book = createBook(author)
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
        val book = createBook(author)

        val readers = mutableSetOf<Int>()
        var reader = createReader()
        createFeedBack(book, reader)
        readers.add(reader.id!!)

        reader = createReader()
        createFeedBack(book, reader)
        readers.add(reader.id!!)

        val response = rest.get("/api/library/books/${book.id}/feedbacks")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
            }.andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<Array<FeedbackDTO>>()
        val feedbacks = mapper.readValue(response, typeRef)

        assertEquals(2, feedbacks.size)

        readers.forEach { readerId ->
            val feedback = feedbacks.first { it.readerId == readerId }
            assertNotNull(feedback)
            assertEquals(book.id, feedback.bookId)
        }
    }

    @Test
    fun searchBooks() {
        val author = createAuthor()
        createBook(author)
        createBook(author)
        createBook(author)
        createBook(author)
        createBook(author)
        createBook(author)

        val searchCriteria = SearchCriteria()
        searchCriteria.title = "title"

        val books = searchBooks(searchCriteria)

        assertTrue { books.isNotEmpty() }
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

        val fromServer = books.filter { it.id == book.id }[0]
        assertEquals(book, fromServer)
    }

    private fun searchBooks(searchCriteria: SearchCriteria): List<BookDTO> {
        val response = rest.post("/api/library/books/search") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(searchCriteria)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
            }
            .andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<PageResultDTO<BookDTO>>()
        val pageResult = mapper.readValue(response, typeRef)
        return pageResult.result
    }
}
