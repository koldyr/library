package com.koldyr.library.services

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.SearchCriteria

/**
 * Description of class BookService
 * @created: 2021-09-28
 */
interface BookService {
    fun findAll(available: Boolean): List<BookDTO>
    fun create(book: BookDTO): Int
    fun findById(bookId: Int): BookDTO
    fun update(bookId: Int, book: BookDTO)
    fun delete(bookId: Int)

    fun findBooks(authorId: Int): List<BookDTO>
    fun findBooks(criteria: SearchCriteria): List<BookDTO>
}
