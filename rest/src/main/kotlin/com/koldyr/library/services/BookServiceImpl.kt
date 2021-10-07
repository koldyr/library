package com.koldyr.library.services

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.model.Book
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.stream.Collectors.*

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

    private fun mapBook(source: BookDTO, target: Book) {
        source.id = target.id!!
        mapper.map(source, target)
    }

    private fun mapBook(book: Book): BookDTO {
        return mapper.map(book, BookDTO::class.java)
    }
}

