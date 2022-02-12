package com.koldyr.library.dto

import com.koldyr.library.model.Role

data class ReaderDTO(
    var id: Int?,
    var firstName: String? = null,
    var lastName: String? = null,
    var mail: String? = null
) {
    var password: String? = null
    var address: String? = null
    var phoneNumber: String? = null
    var note: String? = null
    var roles: MutableSet<Role> = mutableSetOf()
}
