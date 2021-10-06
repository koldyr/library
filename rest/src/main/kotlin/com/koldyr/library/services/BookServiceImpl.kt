package com.koldyr.library.services

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.model.Book
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
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
        private val authorRepository: AuthorRepository
) : BookService {

    override fun findAll(): List<BookDTO> {
        return bookRepository.findAll().stream().map(this::mapBook).collect(toList())
    }

    @Transactional
    override fun create(book: BookDTO): Int {
        val newBook = Book()
        mapBook(book, newBook)
        
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

    private fun mapBook(source: BookDTO, target: Book) {
        if (source.authorId != null) {
            target.author = authorRepository.findById(source.authorId!!)
                    .orElseThrow { ResponseStatusException(NOT_FOUND, "Author with id '${source.authorId}' is not found") }
        }
        target.title = source.title
        target.genre = source.genre
        target.publicationDate = source.publicationDate
        target.publishingHouse = source.publishingHouse
        target.bookCover = source.bookCover
        target.note = source.note
    }

    private fun mapBook(book: Book): BookDTO {
        val dto = BookDTO(book.id!!)
        dto.authorId = book.author?.id
        dto.title = book.title
        dto.genre = book.genre
        dto.publicationDate = book.publicationDate
        dto.publishingHouse = book.publishingHouse
        dto.bookCover = book.bookCover
        dto.note = book.note
        return dto
    }
}

