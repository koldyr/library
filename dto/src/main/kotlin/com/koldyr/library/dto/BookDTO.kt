package com.koldyr.library.dto

import java.time.LocalDate
import jakarta.validation.constraints.Size

/**
 * Description of class BookDTO
 * 
 * @author: d.halitski@gmail.com
 * @created: 2021-09-28
 */
data class BookDTO (
    var id: Int? = null,
    @field:Size(max = 255)
    var title: String? = null,
    var authorId: Int? = null,
    var genres: Set<String>? = null,
    @field:Size(max = 255)
    var publishingHouse: String? = null,
    var publicationDate: LocalDate? = null,
    @field:Size(max = 1024)
    var bookCover: String? = null,
    @field:Size(max = 1024)
    var note: String? = null,
    var count: Int? = null,
)
