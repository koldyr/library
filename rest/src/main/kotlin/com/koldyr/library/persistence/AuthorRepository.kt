package com.koldyr.library.persistence

import com.koldyr.library.model.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Description of class AuthorRepository
 * @created: 2021-10-06
 */
@Repository("authorRepository")
interface AuthorRepository : JpaRepository<Author, Int>
