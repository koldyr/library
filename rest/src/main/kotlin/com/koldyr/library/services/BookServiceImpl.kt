package com.koldyr.library.services

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.SearchCriteria
import com.koldyr.library.model.Book
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Order
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.FeedbackRepository
import com.koldyr.library.persistence.OrderRepository
import com.koldyr.library.persistence.ReaderRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Objects.*
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

/**
 * Description of class BookServiceImpl
 * @created: 2021-09-28
 */
open class BookServiceImpl(
        private val bookRepository: BookRepository,
        private val authorRepository: AuthorRepository,
        private val readerRepository: ReaderRepository,
        private val orderRepository: OrderRepository,
        private val feedbackRepository: FeedbackRepository,
        private val mapper: MapperFacade
) : BookService {

    override fun findAll(available: Boolean): List<BookDTO> {
        if (available) {
            return bookRepository.findAvailable().map(this::mapBook)
        }
        return bookRepository.findAll().map(this::mapBook)
    }

    @Transactional
    override fun create(book: BookDTO): Int {
        book.id = null
        val newBook = mapper.map(book, Book::class.java)

        val saved = bookRepository.save(newBook)
        return saved.id!!
    }

    override fun findById(bookId: Int): BookDTO {
        val book = find(bookId)
        return mapBook(book)
    }

    @Transactional
    override fun update(bookId: Int, book: BookDTO) {
        val persisted: Book = find(bookId)

        mapBook(book, persisted)

        bookRepository.save(persisted)
    }

    @Transactional
    override fun delete(bookId: Int) = bookRepository.deleteById(bookId)

    @Transactional
    override fun feedbackBook(bookId: Int, feedback: FeedbackDTO): Int {
        if (isNull(feedback.readerId)) {
            throw ResponseStatusException(BAD_REQUEST, "Reader id must be provided")
        }

        feedback.bookId = bookId
        feedback.date = LocalDateTime.now()
        val newFeedback = mapper.map(feedback, Feedback::class.java)
        newFeedback.id = null

        feedbackRepository.save(newFeedback)

        return newFeedback.id!!
    }

    override fun bookFeedbacks(bookId: Int): List<FeedbackDTO> {
        return feedbackRepository.findAllByBookId(bookId).map { mapper.map(it, FeedbackDTO::class.java) }
    }

    @Transactional
    override fun takeBook(order: OrderDTO): OrderDTO {
        if (isNull(order.readerId)) {
            throw ResponseStatusException(BAD_REQUEST, "Reader id must be provided")
        }
        if (isNull(order.bookId)) {
            throw ResponseStatusException(BAD_REQUEST, "Book id must be provided")
        }

        if (!bookRepository.existsById(order.bookId!!)) {
            throw ResponseStatusException(NOT_FOUND, "Book with id '${order.bookId}' is not found")
        }
        if (!readerRepository.existsById(order.readerId!!)) {
            throw ResponseStatusException(NOT_FOUND, "Reader with id '${order.readerId}' is not found")
        }

        order.id = null
        order.ordered = LocalDateTime.now()
        val newOrder = mapper.map(order, Order::class.java)
        
        orderRepository.save(newOrder)
        order.id = newOrder.id

        return order
    }

    @Transactional
    override fun returnBook(order: OrderDTO) {
        if (isNull(order.id)) {
            throw ResponseStatusException(BAD_REQUEST, "Order id must be provided")
        }

        val persisted = orderRepository.findById(order.id!!)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Book with id '${order.id}' is not found") }
        
        persisted.returned = LocalDateTime.now()
        if (isNull(persisted.notes)) {
            persisted.notes = order.notes
        } else {
            persisted.notes = persisted.notes + '\n' + order.notes
        }

        val book = find(persisted.bookId!!)
        book.count++

        orderRepository.save(persisted)
        bookRepository.save(book)
    }

    override fun findBooks(authorId: Int): List<BookDTO> {
        if (!authorRepository.existsById(authorId)) {
            throw ResponseStatusException(NOT_FOUND, "Author with id '$authorId' is not found")
        }
        return bookRepository.findBooksByAuthorId(authorId).map(this::mapBook)
    }

    override fun findBooks(criteria: SearchCriteria): List<BookDTO> {
        val filter = createFilter(criteria)
        return bookRepository.findAll(filter).map(this::mapBook)
    }

    private fun createFilter(criteria: SearchCriteria): Specification<Book> {
        return Specification<Book> { book, _, builder ->
            var filter: Predicate? = null
            if (nonNull(criteria.title)) {
                filter = builder.like(book.get("title"), "%${criteria.title}%")
            }

            if (nonNull(criteria.genre)) {
                val genre = book.get<String>("genre")
                val predicate = builder.like(genre, "%${criteria.genre}%")
                filter = if (isNull(filter)) predicate else builder.and(predicate)
            }

            if (nonNull(criteria.publisher)) {
                val publishingHouse = book.get<String>("publishingHouse")
                val predicate = builder.like(publishingHouse, "%${criteria.publisher}%")
                filter = if (isNull(filter)) predicate else builder.and(predicate)
            }

            if (nonNull(criteria.note)) {
                val note = book.get<String>("note")
                val predicate = builder.like(note, "%${criteria.note}%")
                filter = if (isNull(filter)) predicate else builder.and(predicate)
            }

            if (isNull(criteria.publishYear)) {
                if (nonNull(criteria.publishYearFrom) || nonNull(criteria.publishYearTill)) {
                    val publicationDate: Path<LocalDate> = book.get("publicationDate")
                    val from = LocalDate.of(criteria.publishYearFrom!!, 1, 1)
                    val to = LocalDate.of(criteria.publishYearTill!!, 12, 31)
                    val predicate = builder.between(publicationDate, from, to)
                    filter = if (isNull(filter)) predicate else builder.and(predicate)
                }
            } else {
                val publicationDate: Path<LocalDate> = book.get("publicationDate")
                val predicate = builder.equal(publicationDate, criteria.publishYear)
                filter = if (isNull(filter)) predicate else builder.and(predicate)
            }

            filter
        }
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

