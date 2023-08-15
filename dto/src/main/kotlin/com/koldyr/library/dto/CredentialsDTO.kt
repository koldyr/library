package com.koldyr.library.dto

import jakarta.validation.constraints.Size

/**
 * Description of class CredentialsDTO
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-09
 */
class CredentialsDTO(
    @field:Size(max = 255) var username: String?,
    @field:Size(max = 255) var password: String?
) {
    override fun toString(): String {
        return "CredentialsDTO(username=$username)"
    }
}
