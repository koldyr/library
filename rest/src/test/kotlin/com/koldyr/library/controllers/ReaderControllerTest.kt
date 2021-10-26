package com.koldyr.library.controllers

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Reader
import org.junit.Test
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

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
            headers { setBasicAuth(basicHash) }
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
        assertEquals(order, orders[0])
    }

    @Test
    fun feedbacks() {
        val reader = createReader()

        val books = mutableSetOf<Int>()

        var author = createAuthor()
        var book = createBook(author)
        createFeedBack(book, reader)
        books.add(book.id!!)

        author = createAuthor()
        book = createBook(author)
        createFeedBack(book, reader)
        books.add(book.id!!)

        val response = rest.get("/api/library/readers/${reader.id}/feedbacks") {
            accept = APPLICATION_JSON
            headers { setBasicAuth(basicHash) }
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                }.andReturn().response.contentAsString

        val typeRef = jacksonTypeRef<Array<FeedbackDTO>>()
        val feedbacks = mapper.readValue(response, typeRef)

        assertEquals(2, feedbacks.size)

        books.forEach { bookId ->
            val feedback = feedbacks.first { it.bookId == bookId }
            assertNotNull(feedback)
            assertEquals(reader.id, feedback.readerId)
        }
    }

    private fun getReader(readerId: Int): Reader {
        val response = rest.get("/api/library/readers/${readerId}") {
            headers { setBasicAuth(basicHash) }
        }
                .andDo { print() }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

        return mapper.readValue(response, Reader::class.java)
    }

    private fun updateReader(reader: Reader) {
        rest.put("/api/library/readers/${reader.id}") {
            contentType = APPLICATION_JSON
            headers { setBasicAuth(basicHash) }
            content = mapper.writeValueAsString(reader)
        }
                .andExpect { status { isOk() } }
    }

    private fun deleteReader(readerId: Int) {
        rest.delete("/api/library/readers/${readerId}") {
            headers { setBasicAuth(basicHash) }
        }
                .andExpect { status { isNoContent() } }
    }
}
