package com.koldyr.library.services

import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO

/**
 * Description of class AuthorService
 * @created: 2021-10-06
 */
interface AuthorService {
    fun findAll(): List<AuthorDTO>
    fun create(author: AuthorDTO): Int
    fun findById(authorId: Int): AuthorDTO
    fun update(authorId: Int, author: AuthorDTO)
    fun delete(authorId: Int)
    fun addBook(authorId: Int, book: BookDTO): Int
}
