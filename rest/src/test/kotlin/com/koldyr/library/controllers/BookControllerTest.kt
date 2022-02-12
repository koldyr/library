package com.koldyr.library.controllers

import java.util.Objects.nonNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.library.controllers.TestDbInitializer.token
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.PageDTO
import com.koldyr.library.dto.PageResultDTO
import com.koldyr.library.dto.SearchCriteria
import com.koldyr.library.dto.SortDTO
import com.koldyr.library.model.Order
import com.koldyr.library.persistence.OrderRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

/**
 * Description of class BookControllerTest
 * @created: 2021-10-23
 */
class BookControllerTest: LibraryControllerTest() {

    @Autowired
    lateinit var orderRepository: OrderRepository

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

        rest.get("/api/library/books/${book.id}") {
            header(AUTHORIZATION, token!!)
        }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun orderBooks() {
        val currentUser = getCurrentUser()

        var allBooks: List<BookDTO> = findAllBooks()
        if (allBooks.isEmpty()) {
            allBooks = mutableListOf()
            val author = createAuthor()
            for (i in 0 until 10) {
                allBooks.add(createBook(author))
            }
        }

        val orders: MutableList<OrderDTO> = mutableListOf()
        for (book in allBooks) {
            val order = takeBook(book)
            orders.add(order)
        }

        for (order in orders) {
            returnBook(order)
        }

        for (book in allBooks) {
            val completed: Collection<Order> = getBookOrders(book.id!!)

            val bookOrders = completed.filter { order ->
                order.reader!!.id == currentUser.id &&
                order.bookId == book.id &&
                nonNull(order.ordered)  &&
                nonNull(order.returned)
            }
            assertTrue(bookOrders.size > 0)
        }
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

        for (book in allBooks) {
            val feedbacks = getBookFeedbacks(book.id!!)

            val readerFeedbacks = feedbacks.filter { feedback -> feedback.readerId == currentUser.id && feedback.bookId == book.id }
            assertTrue(readerFeedbacks.size > 0)
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
        searchCriteria.sort = SortDTO("title", "DESC")
        searchCriteria.page = PageDTO(5)
        searchCriteria.title = "title"

        val books = searchBooks(searchCriteria)

        assertTrue { books.isNotEmpty() }
    }

    private fun readBook(bookId: Int): BookDTO {
        val body: String = rest.get("/api/library/books/$bookId") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
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
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(book)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
            }
    }

    private fun deleteBook(bookId: Int) {
        rest.delete("/api/library/books/$bookId") {
            header(AUTHORIZATION, token!!)
        }
            .andExpect {
                status { isNoContent() }
            }
    }

    private fun assertBooks(book: BookDTO) {
        val response = rest.get("/api/library/books") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
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
            header(AUTHORIZATION, token!!)
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

    private fun getBookFeedbacks(bookId: Int): Array<FeedbackDTO> {
        val response = rest.get("/api/library/books/$bookId/feedbacks") {
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

    private fun takeBook(book: BookDTO): OrderDTO {
        val order = OrderDTO()
        order.bookId = book.id
        order.notes = "taken"

        val response = rest.post("/api/library/books/take") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(order)
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                content { contentType(APPLICATION_JSON) }
            }.andReturn().response.contentAsString

        return mapper.readValue(response, OrderDTO::class.java)
    }

    private fun returnBook(order: OrderDTO) {
        rest.post("/api/library/books/return") {
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, token!!)
            content = mapper.writeValueAsString(order)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
            }
    }

    private fun getBookOrders(bookId: Int): Collection<Order> {
        return orderRepository.findOrdersByBookId(bookId)
    }
}
