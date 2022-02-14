package com.koldyr.library.persistence

import com.koldyr.library.model.Genre
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Description of class GenreRepository
 * @created: 2022-02-14
 */
@Repository("genreRepository")
interface GenreRepository : JpaRepository<Genre, Int> {
    fun findByName(name: String): Optional<Genre>
}
