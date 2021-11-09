package com.koldyr.library.services

import java.time.LocalDate
import java.time.LocalDate.of
import java.time.LocalDateTime
import java.util.Objects.isNull
import java.util.Objects.nonNull
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.PageResultDTO
import com.koldyr.library.dto.ReaderDetails
import com.koldyr.library.dto.SearchCriteria
import com.koldyr.library.model.Book
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Genre
import com.koldyr.library.model.Order
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.FeedbackRepository
import com.koldyr.library.persistence.OrderRepository
import ma.glasnost.orika.MapperFacade
import org.apache.commons.lang3.ArrayUtils.isNotEmpty
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.full.declaredMemberProperties

/**
 * Description of class BookServiceImpl
 * @created: 2021-09-28
 */
open class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val orderRepository: OrderRepository,
    private val feedbackRepository: FeedbackRepository,
    private val mapper: MapperFacade
) : BookService {

    @PreAuthorize("hasAuthority('read_book')")
    override fun findAll(available: Boolean): List<BookDTO> {
        if (available) {
            return bookRepository.findAvailable().map(this::mapBook)
        }
        return bookRepository.findAll().map(this::mapBook)
    }

    @Transactional
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

    @Transactional
    @PreAuthorize("hasAuthority('modify_book')")
    override fun update(bookId: Int, book: BookDTO) {
        val persisted = find(bookId)

        mapBook(book, persisted)

        bookRepository.save(persisted)
    }

    @Transactional
    @PreAuthorize("hasAuthority('modify_book')")
    override fun delete(bookId: Int) {
        if (orderRepository.hasOrders(bookId)) {
            throw ResponseStatusException(BAD_REQUEST, "Unable to delete book '${bookId}', it is already ordered")
        }
        bookRepository.deleteById(bookId)
    }

    @Transactional
    @PreAuthorize("hasAuthority('modify_feedback')")
    override fun feedbackBook(feedback: FeedbackDTO): Int {
        feedback.date = LocalDateTime.now()
        feedback.readerId = getLoggedUserId()

        val newFeedback = mapper.map(feedback, Feedback::class.java)
        newFeedback.id = null

        feedbackRepository.save(newFeedback)

        return newFeedback.id!!
    }

    @PreAuthorize("hasAuthority('read_feedback')")
    override fun bookFeedbacks(bookId: Int): List<FeedbackDTO> {
        return feedbackRepository.findAllByBookId(bookId).map { mapper.map(it, FeedbackDTO::class.java) }
    }

    @Transactional
    override fun deleteFeedback(feedbackId: Int) {
        val feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow { throw ResponseStatusException(NOT_FOUND, "Feedback with id '${feedbackId}' is not found") }

        if (feedback.reader!!.id == getLoggedUserId() || hasAuthority("modify_feedback")) {
            feedbackRepository.delete(feedback)
        }
    }

    @Transactional
    @PreAuthorize("hasAuthority('order_book')")
    override fun takeBook(order: OrderDTO): OrderDTO {
        val book = find(order.bookId!!)

        if (book.count == 0) {
            throw ResponseStatusException(BAD_REQUEST, "Book '${book.title}' is out of stock")
        }

        order.id = null
        order.ordered = LocalDateTime.now()
        order.readerId = getLoggedUserId()

        val newOrder = mapper.map(order, Order::class.java)

        orderRepository.save(newOrder)
        order.id = newOrder.id

        book.count--
        bookRepository.save(book)

        return order
    }

    @Transactional
    @PreAuthorize("hasAuthority('order_book')")
    override fun returnBook(order: OrderDTO) {
        val persisted = orderRepository.findById(order.id!!)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Order with id '${order.id}' is not found") }

        if (persisted.reader!!.id != getLoggedUserId()) {
            throw ResponseStatusException(BAD_REQUEST, "You can not return order with id '${order.id}', only reader ${getLoggedUserId()}")
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

        val filter = createFilter(criteria)
        val pageSelector: Pageable = createPageable(criteria)
        val booksPage = bookRepository.findAll(filter, pageSelector)

        return createPageResult(booksPage)
    }

    private fun hasCriteria(criteria: SearchCriteria?): Boolean {
        if (criteria == null) {
            return false
        }
        return SearchCriteria::class.declaredMemberProperties
            .filter { it.name != "page" && it.name != "sort" }
            .any { nonNull(it.get(criteria)) }
    }

    private fun createFilter(criteria: SearchCriteria): Specification<Book>? {
        if (!hasCriteria(criteria)) {
            return null
        }
        return Specification<Book> { book, _, builder ->
            var filter: Predicate? = null
            if (nonNull(criteria.title)) {
                filter = builder.like(builder.lower(book.get("title")), "%${criteria.title?.lowercase()}%")
            }

            if (isNotEmpty(criteria.genre)) {
                val values: List<Genre> = criteria.genre!!.filter { nonNull(it) }.map { Genre.valueOf(it.uppercase()) }
                if (values.isNotEmpty()) {
                    val genre = book.get<Genre>("genre")
                    val predicate: Predicate = genre.`in`(values)
                    filter = if (isNull(filter)) predicate else builder.and(filter, predicate)
                }
            }

            if (nonNull(criteria.publisher)) {
                val publishingHouse = book.get<String>("publishingHouse")
                val predicate = builder.like(builder.lower(publishingHouse), "%${criteria.publisher?.lowercase()}%")
                filter = if (isNull(filter)) predicate else builder.and(filter, predicate)
            }

            if (nonNull(criteria.note)) {
                val note = book.get<String>("note")
                val predicate = builder.like(builder.lower(note), "%${criteria.note?.lowercase()}%")
                filter = if (isNull(filter)) predicate else builder.and(filter, predicate)
            }

            if (nonNull(criteria.publishYearFrom) || nonNull(criteria.publishYearTill)) {
                val yearFrom: Int = if (criteria.publishYearFrom == null) 1000 else criteria.publishYearFrom!!
                val yearTo: Int = if (criteria.publishYearTill == null) 9999 else (criteria.publishYearTill!! + 1)
                val publicationDate: Path<LocalDate> = book.get("publicationDate")
                val predicate = builder.between(publicationDate, of(yearFrom, 1, 1), of(yearTo, 1, 1))
                filter = if (isNull(filter)) predicate else builder.and(predicate)
            }

            filter
        }
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

    private fun getLoggedUserId(): Int {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication
        val readerDetails = authentication.principal as ReaderDetails
        return readerDetails.getReaderId()
    }

    private fun hasAuthority(authority: String): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication
        val readerDetails = authentication.principal as ReaderDetails
        return readerDetails.hasAuthority(authority)
    }
}

