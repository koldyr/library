package com.koldyr.library.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import com.koldyr.library.model.Author

/**
 * Description of class AuthorRepository
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-06
 */
@Repository("authorRepository")
interface AuthorRepository : JpaRepository<Author, Int>, JpaSpecificationExecutor<Author>
