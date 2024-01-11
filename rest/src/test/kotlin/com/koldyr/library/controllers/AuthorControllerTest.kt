package com.koldyr.library.controllers

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.apache.commons.lang3.RandomUtils
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import com.koldyr.library.controllers.TestDbInitializer.token
import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO


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

        rest.get("/api/v1/authors/${author.id}") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isNotFound() } }
    }

    @Test
    fun books() {
        val author = createAuthor()

        val books: MutableCollection<BookDTO> = mutableSetOf()
        books.add(createBook(author))
        books.add(createBook(author))
        books.add(createBook(author))

        val body: String = rest.get("/api/v1/authors/${author.id}/books") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
        }
//                .andDo { print() }
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

    @Test
    fun search() {
        val authors = mutableListOf(
            createAuthor(),
            createAuthor(),
            createAuthor(),
            createAuthor(),
            createAuthor()
        )

        val author = authors[RandomUtils.nextInt(0, authors.size - 1)]
        val firstName = author.firstName

        val body: String = rest.get("/api/v1/authors") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            param("search", firstName!!)
        }
//                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                }.andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<List<AuthorDTO>>()
        val foundAuthors = mapper.readValue(body, typeRef)

        assertTrue(foundAuthors.any { it.id == author.id})
    }

    private fun getAuthor(authorId: Int): AuthorDTO {
        val body: String = rest.get("/api/v1/authors/$authorId") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
        }
//                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                }.andReturn().response.contentAsString

        val dto = mapper.readValue(body, AuthorDTO::class.java)
        dto.books = dto.books.toMutableSet()
        return dto
    }

    private fun updateAuthor(author: AuthorDTO) {
        rest.put("/api/v1/authors/${author.id}") {
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(author)
        }
//                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }
    }

    private fun deleteAuthor(authorId: Int) {
        rest.delete("/api/v1/authors/$authorId") {
            header(AUTHORIZATION, token!!)
        }
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
