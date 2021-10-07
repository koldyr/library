package com.koldyr.library.services

import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.model.Author

/**
 * Description of class AuthorService
 * @created: 2021-10-06
 */
interface AuthorService {
    fun findAll(): List<AuthorDTO>
    fun create(author: AuthorDTO): Int
    fun findById(authorId: Int): Author
    fun update(authorId: Int, author: AuthorDTO)
    fun delete(authorId: Int)
}
