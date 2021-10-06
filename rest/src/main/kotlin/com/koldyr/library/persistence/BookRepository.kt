package com.koldyr.library.persistence

import com.koldyr.library.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Description of class BookRepository
 * @created: 2021-09-25
 */
@Repository("bookRepository")
interface BookRepository : JpaRepository<Book, Int>
