package com.koldyr.library.mapper

import com.koldyr.library.model.Book
import com.koldyr.library.persistence.BookRepository
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.converter.BidirectionalConverter
import ma.glasnost.orika.metadata.Type
import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException

class BookConverter(private val bookRepository: BookRepository) : BidirectionalConverter<Int, Book>() {

    override fun convertTo(bookId: Int?, type: Type<Book>?, context: MappingContext?): Book? {
        if (bookId == null) {
            return null
        }
        return bookRepository.findById(bookId)
                .orElseThrow { ResponseStatusException(BAD_REQUEST, "Book with id '$bookId' is not found") }
    }

    override fun convertFrom(book: Book?, type: Type<Int>?, context: MappingContext?): Int? {
        return book?.id
    }
}
