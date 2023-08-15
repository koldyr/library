package com.koldyr.library.dto

import jakarta.validation.constraints.Size

data class ReaderDTO(
    var id: Int?,
    @field:Size(max = 255)
    var firstName: String? = null,
    @field:Size(max = 255)
    var lastName: String? = null,
    @field:Size(max = 255)
    var mail: String? = null
) {
    @field:Size(max = 255)
    var password: String? = null
    @field:Size(max = 255)
    var address: String? = null
    @field:Size(max = 255)
    var phoneNumber: String? = null
    @field:Size(max = 255)
    var note: String? = null
    var roles: MutableSet<String> = mutableSetOf()

    constructor() : this(null, null, null, null)
}
