package com.koldyr.library.dto

import java.time.LocalDate

/**
 * Description of class AuthorDTO
 * @created: 2021-10-07
 */
data class AuthorDTO (
    var id: Int? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var dateOfBirth: LocalDate? = null,
    var books: MutableCollection<Int> = mutableSetOf()
)
