package com.koldyr.library.dto

import java.time.LocalDate
import com.koldyr.library.model.Genre

/**
 * Description of class BookDTO
 * @created: 2021-09-28
 */
data class BookDTO (
    var id: Int? = null,
    var title: String? = null,
    var authorId: Int? = null,
    var genre: Genre? = null,
    var publishingHouse: String? = null,
    var publicationDate: LocalDate? = null,
    var bookCover: String? = null,
    var note: String? = null,
    var count: Int? = null,
)
