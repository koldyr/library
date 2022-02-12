package com.koldyr.library.controllers

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.controllers.TestDbInitializer.token
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Reader
import org.junit.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Description of class ReaderControllerTest
 * @created: 2021-10-24
 */
class ReaderControllerTest : LibraryControllerTest() {

    @Test
    fun readers() {
        val reader: Reader = createReader()
        assertNotNull(reader.id)

        var readerFromServer: Reader = getReader(reader.id!!)
        assertEquals(reader, readerFromServer)

        reader.firstName = "r1_fname_new"
        reader.lastName = "r1_lname_new"
        reader.mail = "r1_mail_new"
        reader.address = "r1_address_new"
        reader.phoneNumber = "r1_phone_new"
        reader.note = "r1_note_new"

        updateReader(reader)

        readerFromServer = getReader(reader.id!!)
        assertEquals(reader, readerFromServer)

        val readers = findAllReaders()

        val filtered = readers.filter { it.id == reader.id }
        assertFalse { filtered.isEmpty() }
        readerFromServer = filtered[0]
        assertEquals(reader, readerFromServer)

        deleteReader(reader.id!!)

        rest.get("/api/library/readers/${reader.id}") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isNotFound() } }
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

    private fun getReader(readerId: Int): Reader {
        val response = rest.get("/api/library/readers/${readerId}") {
            header(AUTHORIZATION, token!!)
        }
                .andDo { print() }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        return mapper.readValue(response, Reader::class.java)
    }

    private fun updateReader(reader: Reader) {
        rest.put("/api/library/readers/${reader.id}") {
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(reader)
        }
                .andExpect { status { isOk() } }
    }

    private fun deleteReader(readerId: Int) {
        rest.delete("/api/library/readers/${readerId}") {
            header(AUTHORIZATION, token!!)
        }
                .andExpect { status { isNoContent() } }
    }

    private fun getReadersFeedbacks(currentUser: Reader): Array<FeedbackDTO> {
        val response = rest.get("/api/library/readers/${currentUser.id}/feedbacks") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                }.andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<Array<FeedbackDTO>>()
        return mapper.readValue(response, typeRef)
    }
}
