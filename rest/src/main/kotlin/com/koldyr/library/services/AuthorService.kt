package com.koldyr.library.services

import com.koldyr.library.model.Author

/**
 * Description of class AuthorService
 * @created: 2021-10-06
 */
interface AuthorService {
    fun findAll(): List<Author>
    fun create(author: Author): Int
    fun findById(authorId: Int): Author
    fun update(authorId: Int, author: Author)
    fun delete(authorId: Int)
}
