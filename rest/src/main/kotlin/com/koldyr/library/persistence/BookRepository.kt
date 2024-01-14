package com.koldyr.library.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import com.koldyr.library.model.Book

/**
 * Description of class BookRepository
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-09-25
 */
@Repository("bookRepository")
interface BookRepository : JpaRepository<Book, Int>, JpaSpecificationExecutor<Book> {

    @Query("from Book b where b.archived = :archived")
    fun findAllBooks(@Param("archived") archived: Boolean = false): List<Book>

    fun findAllByAuthorId(authorId: Int): List<Book>

    @Query("from Book b where b.count > 0 and not b.archived")
    fun findAllAvailableBooks(): List<Book>

    @Query("from Book b where b.count > 0 and b.author.id = :authorId and not b.archived")
    fun findAvailableForAuthor(@Param("authorId") authorId: Int): List<Book>
}
