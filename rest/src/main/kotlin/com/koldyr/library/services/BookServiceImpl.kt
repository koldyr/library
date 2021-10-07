package com.koldyr.library.services

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.SearchCriteria
import com.koldyr.library.model.Book
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository

import ma.glasnost.orika.MapperFacade
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

import java.time.LocalDate
import java.util.Objects.*
import java.util.stream.Collectors.*
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate

/**
 * Description of class BookServiceImpl
 * @created: 2021-09-28
 */
open class BookServiceImpl(
        private val bookRepository: BookRepository,
        private val authorRepository: AuthorRepository,
        private val mapper: MapperFacade) : BookService {

    override fun findAll(available: Boolean): List<BookDTO> {
        if (available) {
            return bookRepository.findAvailable().stream().map(this::mapBook).collect(toList())
        }
        return bookRepository.findAll().stream().map(this::mapBook).collect(toList())
    }

    @Transactional
    override fun create(book: BookDTO): Int {
        book.id = null
        val newBook = mapper.map(book, Book::class.java)

        val saved = bookRepository.save(newBook)
        return saved.id!!
    }

    override fun findById(bookId: Int): BookDTO {
        return bookRepository.findById(bookId)
                .map(this::mapBook)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Book with id '$bookId' is not found") }
    }

    @Transactional
    override fun update(bookId: Int, book: BookDTO) {
        val persisted: Book = bookRepository.findById(bookId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Book with id '$bookId' is not found") }

        mapBook(book, persisted)

        bookRepository.save(persisted)
    }

    @Transactional
    override fun delete(bookId: Int) = bookRepository.deleteById(bookId)

    override fun findBooks(authorId: Int): List<BookDTO> {
        if (!authorRepository.existsById(authorId)) {
            throw ResponseStatusException(NOT_FOUND, "Author with id '$authorId' is not found")
        }
        return bookRepository.findBooksByAuthorId(authorId).stream()
                .map(this::mapBook)
                .collect(toList())
    }

    override fun findBooks(criteria: SearchCriteria): List<BookDTO> {
        val filter = createFilter(criteria)
        return bookRepository.findAll(filter).stream()
                .map(this::mapBook)
                .collect(toList())
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
                    val from = LocalDate.of(criteria.publishYearFrom!!, 1 ,1)
                    val to = LocalDate.of(criteria.publishYearTill!!, 12,31)
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
}

