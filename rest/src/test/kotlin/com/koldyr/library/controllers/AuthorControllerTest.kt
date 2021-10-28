package com.koldyr.library.controllers

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO
import org.junit.Test
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


/**
 * Description of class AuthorControllerTest
 * @created: 2021-10-21
 */
class AuthorControllerTest : LibraryControllerTest() {

    @Test
    fun authors() {
        val author = createAuthor()
        assertNotNull(author.id)

        var authorFromServer = getAuthor(author.id!!)
        assertEquals(author, authorFromServer)

        author.firstName = author.firstName + " new"
        author.lastName = author.lastName + " new"

        updateAuthor(author)

        authorFromServer = getAuthor(author.id!!)
        assertEquals(author, authorFromServer)

        assertAuthors(author)

        deleteAuthor(author.id!!)

        rest.get("/api/library/authors/${author.id}")
                .andExpect { status { isNotFound() } }
    }

    @Test
    fun books() {
        val author = createAuthor()

        val books: MutableCollection<BookDTO> = mutableSetOf()
        books.add(createBook(author))
        books.add(createBook(author))
        books.add(createBook(author))

        val body: String = rest.get("/api/library/authors/${author.id}/books") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                }.andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<BookDTO>>()
        val booksFromServer = mapper.readValue(body, typeRef)

        books.forEach { book ->
            val dto = booksFromServer.first { it.id == book.id }
            assertEquals(book, dto)
        }
    }

    private fun getAuthor(authorId: Int): AuthorDTO {
        val body: String = rest.get("/api/library/authors/$authorId") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                }.andReturn().response.contentAsString

        val dto = mapper.readValue(body, AuthorDTO::class.java)
        dto.books = dto.books.toMutableSet()
        return dto
    }

    private fun updateAuthor(author: AuthorDTO) {
        rest.put("/api/library/authors/${author.id}") {
            contentType = APPLICATION_JSON
            content = mapper.writeValueAsString(author)
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }
    }

    private fun deleteAuthor(authorId: Int) {
        rest.delete("/api/library/authors/$authorId")
                .andExpect {
                    status { isNoContent() }
                }
    }

    private fun assertAuthors(author: AuthorDTO) {
        val authors = findAllAuthors()

        val fromServer = authors.first { it.id == author.id }
        assertNotNull(fromServer)

        fromServer.books = fromServer.books.toMutableSet()
        assertEquals(author, fromServer)
    }
}
