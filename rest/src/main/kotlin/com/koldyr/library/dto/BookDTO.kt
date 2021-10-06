package com.koldyr.library.dto

import com.koldyr.library.model.Genre
import java.time.LocalDate

/**
 * Description of class BookDTO
 * @created: 2021-09-28
 */
class BookDTO(val id: Int) {
    var title: String? = null
    var authorId: Int? = null
    var genre: Genre? = null
    var publishingHouse: String? = null
    var publicationDate: LocalDate? = null
    var bookCover: String? = null
    var note: String? = null
}
