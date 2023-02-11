package com.koldyr.library.services

import java.time.LocalDateTime
import java.util.Objects.isNull
import java.util.Objects.nonNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import ma.glasnost.orika.MapperFacade
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.PageResultDTO
import com.koldyr.library.dto.SearchCriteria
import com.koldyr.library.model.Book
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Order
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.FeedbackRepository
import com.koldyr.library.persistence.OrderRepository

/**
 * Description of class BookServiceImpl
 * @created: 2021-09-28
 */
@Service
@Transactional
open class BookServiceImpl(
    bookRepository: BookRepository,
    mapper: MapperFacade,
    private val authorRepository: AuthorRepository,
    private val orderRepository: OrderRepository,
    private val feedbackRepository: FeedbackRepository,
    private val predicateBuilder: PredicateBuilder,
) : BookService, BaseLibraryService(bookRepository, mapper) {

    @PreAuthorize("hasAuthority('read_book')")
    override fun findAll(available: Boolean): List<BookDTO> {
        if (available) {
            return bookRepository.findAvailable().map(this::mapBook)
        }
        return bookRepository.findAll().map(this::mapBook)
    }

    @PreAuthorize("hasAuthority('modify_book')")
    override fun create(book: BookDTO): Int {
        book.id = null
        val newBook = mapper.map(book, Book::class.java)

        val saved = bookRepository.save(newBook)
        return saved.id!!
    }

    @PreAuthorize("hasAuthority('read_book')")
    override fun findById(bookId: Int): BookDTO {
        val book = find(bookId)
        return mapBook(book)
    }

    @PreAuthorize("hasAuthority('modify_book')")
    override fun update(bookId: Int, book: BookDTO) {
        val persisted = find(bookId)

        mapBook(book, persisted)

        bookRepository.save(persisted)
    }

    @PreAuthorize("hasAuthority('modify_book')")
    override fun delete(bookId: Int) {
        if (orderRepository.hasOrders(bookId)) {
            throw ResponseStatusException(BAD_REQUEST, "Unable to delete book '${bookId}', it is already ordered")
        }
        bookRepository.deleteById(bookId)
    }

    @PreAuthorize("hasAuthority('modify_feedback')")
    override fun feedbackBook(feedback: FeedbackDTO): Int {
        feedback.date = LocalDateTime.now()
        feedback.readerId = getLoggedUser().id

        val newFeedback = mapper.map(feedback, Feedback::class.java)
        newFeedback.id = null

        feedbackRepository.save(newFeedback)

        return newFeedback.id!!
    }

    @PreAuthorize("hasAuthority('read_feedback')")
    override fun bookFeedbacks(bookId: Int): List<FeedbackDTO> {
        return feedbackRepository.findAllByBookId(bookId).map { mapper.map(it, FeedbackDTO::class.java) }
    }

    override fun deleteFeedback(feedbackId: Int) {
        val feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow { throw ResponseStatusException(NOT_FOUND, "Feedback with id '${feedbackId}' is not found") }

        if (feedback.reader!!.id == getLoggedUser().id || hasAuthority("modify_feedback")) {
            feedbackRepository.delete(feedback)
        }
    }

    @PreAuthorize("hasAuthority('order_book')")
    override fun takeBook(order: OrderDTO): OrderDTO {
        val book = find(order.bookId!!)

        if (book.count == 0) {
            throw ResponseStatusException(BAD_REQUEST, "Book '${book.title}' is out of stock")
        }

        order.id = null
        order.ordered = LocalDateTime.now()
        order.readerId = getLoggedUser().id

        val newOrder = mapper.map(order, Order::class.java)

        orderRepository.save(newOrder)
        order.id = newOrder.id

        book.count--
        bookRepository.save(book)

        return order
    }

    @PreAuthorize("hasAuthority('order_book')")
    override fun returnBook(order: OrderDTO) {
        val persisted = orderRepository.findById(order.id!!)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Order with id '${order.id}' is not found") }

        if (persisted.reader!!.id != getLoggedUser().id) {
            throw ResponseStatusException(BAD_REQUEST, "You can not return order with id '${order.id}', only reader ${getLoggedUser().id}")
        }
        if (nonNull(persisted.returned)) {
            throw ResponseStatusException(BAD_REQUEST, "You can not return already returned order '${order.id}'")
        }

        persisted.returned = LocalDateTime.now()
        persisted.notes = if (isNull(persisted.notes)) order.notes else persisted.notes + '\n' + order.notes

        val book = find(persisted.bookId!!)
        book.count++

        orderRepository.save(persisted)
        bookRepository.save(book)
    }

    @PreAuthorize("hasAuthority('read_book')")
    override fun findBooks(authorId: Int): List<BookDTO> {
        if (!authorRepository.existsById(authorId)) {
            throw ResponseStatusException(NOT_FOUND, "Author with id '$authorId' is not found")
        }
        return bookRepository.findBooksByAuthorId(authorId).map(this::mapBook)
    }

    @PreAuthorize("hasAuthority('read_book')")
    override fun findBooks(criteria: SearchCriteria?): PageResultDTO<BookDTO> {
        if (criteria == null) {
            val books = bookRepository.findAll()

            val pageResult = PageResultDTO(books.map(this::mapBook))
            pageResult.total = books.size.toLong()
            return pageResult
        }

        val filter = predicateBuilder.booksFilter(criteria)
        val pageSelector: Pageable = createPageable(criteria)
        val booksPage = bookRepository.findAll(filter, pageSelector)

        return createPageResult(booksPage)
    }

    override fun bookOrders(bookId: Int): Collection<OrderDTO> {
        return orderRepository
                .findOrdersByBookId(bookId)
                .map(this::mapOrder)
    }

    private fun createPageable(criteria: SearchCriteria): Pageable {
        val page: Int = if (criteria.page == null) 0 else criteria.page!!.index
        val size: Int = if (criteria.page == null) 100 else criteria.page!!.size

        val direction = if (criteria.sort == null) Sort.Direction.ASC else Sort.Direction.fromString(criteria.sort!!.order)
        val property = if (criteria.sort == null) "id" else criteria.sort!!.name

        return PageRequest.of(page, size, direction, property)
    }

    private fun createPageResult(booksPage: Page<Book>): PageResultDTO<BookDTO> {
        val books = booksPage.map(this::mapBook).content
        val pageResult = PageResultDTO<BookDTO>(books)
        pageResult.total = booksPage.totalElements
        pageResult.page = booksPage.number
        pageResult.size = booksPage.size
        return pageResult
    }

    private fun mapBook(source: BookDTO, target: Book) {
        source.id = target.id!!
        mapper.map(source, target)
    }

    private fun mapBook(book: Book): BookDTO {
        return mapper.map(book, BookDTO::class.java)
    }

    private fun find(bookId: Int): Book {
        return bookRepository.findById(bookId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Book with id '$bookId' is not found") }
    }
}

