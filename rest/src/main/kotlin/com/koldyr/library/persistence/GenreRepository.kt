package com.koldyr.library.persistence

import java.util.Optional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.koldyr.library.model.Genre

/**
 * Description of class GenreRepository
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-14
 */
@Repository("genreRepository")
interface GenreRepository : JpaRepository<Genre, Int> {
    fun findByName(name: String): Optional<Genre>
}
