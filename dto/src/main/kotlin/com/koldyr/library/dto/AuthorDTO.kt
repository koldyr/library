package com.koldyr.library.dto

import java.time.LocalDate
import jakarta.validation.constraints.Size

/**
 * Description of class AuthorDTO
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-07
 */
data class AuthorDTO (
    var id: Int? = null,
    @field:Size(max = 255)
    var firstName: String? = null,
    @field:Size(max = 255)
    var lastName: String? = null,
    var dateOfBirth: LocalDate? = null,
    var books: MutableCollection<Int> = mutableSetOf()
)
