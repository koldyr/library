package com.koldyr.library.controllers

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.controllers.TestDbInitializer.token
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.ReaderDTO

/**
 * Description of class ReaderControllerTest
 * @created: 2021-10-24
 */
class ReaderControllerTest : LibraryControllerTest() {

    @Test
    fun readers() {
        val reader: ReaderDTO = createReader()
        assertNotNull(reader.id)

        val initialEmail = reader.mail
        var readerFromServer: ReaderDTO = getReader(reader.id!!)
        assertEquals(reader, readerFromServer)

        val index = (1 until 10).random()
        reader.firstName = "r1_fname_$index"
        reader.lastName = "r1_lname_$index"
        reader.mail = "r1_mail_$index"
        reader.address = "r1_address_$index"
        reader.phoneNumber = "r1_phone_$index"
        reader.note = "r1_note_$index"

        updateReader(reader)

        reader.mail = initialEmail
        readerFromServer = getReader(reader.id!!)
        assertEquals(reader, readerFromServer)

        val readers = findAllReaders()

        val filtered = readers.filter { it.id == reader.id }
        assertFalse { filtered.isEmpty() }
        readerFromServer = filtered[0]
        assertEquals(reader, readerFromServer)

        deleteReader(reader.id!!)

        rest.get("/library/readers/${reader.id}") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isNotFound() } }
    }

    @Test
    fun roles() {
        val reader: ReaderDTO = createReader()
        assertNotNull(reader.id)
        val initialEmail = reader.mail

        var readerFromServer: ReaderDTO = getReader(reader.id!!)
        assertEquals(reader, readerFromServer)
        assertEquals(1, readerFromServer.roles.size)
        assertEquals("reader", readerFromServer.roles.first())

        val index = (10 until 20).random()
        reader.firstName = "r1_fname_$index"
        reader.lastName = "r1_lname_$index"
        reader.mail = "r1_mail_$index"
        reader.address = "r1_address_$index"
        reader.phoneNumber = "r1_phone_$index"
        reader.note = "r1_note_$index"
        reader.roles = mutableSetOf("librarian")

        updateReader(reader)

        readerFromServer = getReader(reader.id!!)

        reader.mail = initialEmail
        assertEquals(reader, readerFromServer)
        assertEquals(1, readerFromServer.roles.size)
        assertEquals("librarian", readerFromServer.roles.first())
    }

    @Test
    fun orders() {
        val readers = findAllReaders()
        val books = findAllBooks()

        val author = createAuthor()
        val reader = if (readers.isEmpty()) createReader() else readers[0]
        val book = if (books.isEmpty()) createBook(author) else books[0]
        val order = createOrder(book.id!!, reader.id!!)

        val orders: List<OrderDTO> = getOrdersForReader(reader)

        val filtered = orders.filter { it.id == order.id }
        assertFalse(filtered.isEmpty())

        book.count = book.count?.minus(1)
        order.book = book
        assertEquals(order, orders.last())
    }

    @Test
    fun feedbacks() {
        val currentUser = getCurrentUser()

        var allBooks: List<BookDTO> = findAllBooks()
        if (allBooks.isEmpty()) {
            allBooks = mutableListOf()
            val author = createAuthor()
            for (i in 0 until 10) {
                allBooks.add(createBook(author))
            }
        }

        for (book in allBooks) {
            createFeedBack(book, currentUser)
        }

        val feedbacks = getReadersFeedbacks(currentUser)

        for (book in allBooks) {
            val readerFeedbacks = feedbacks.filter { feedback -> feedback.readerId == currentUser.id && feedback.bookId == book.id }
            assertTrue(readerFeedbacks.size > 0)
        }
    }

    private fun getReader(readerId: Int): ReaderDTO {
        val response = rest.get("/library/readers/${readerId}") {
            header(AUTHORIZATION, token!!)
        }
//                .andDo { print() }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        return mapper.readValue(response, ReaderDTO::class.java)
    }

    private fun updateReader(reader: ReaderDTO) {
        rest.put("/library/readers/${reader.id}") {
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(reader)
        }
                .andExpect { status { isOk() } }
    }

    private fun deleteReader(readerId: Int) {
        rest.delete("/library/readers/${readerId}") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isNoContent() } }
    }

    private fun getReadersFeedbacks(currentUser: ReaderDTO): Array<FeedbackDTO> {
        val response = rest.get("/library/readers/${currentUser.id}/feedbacks") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
        }
//                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                }.andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<Array<FeedbackDTO>>()
        return mapper.readValue(response, typeRef)
    }
}
