package com.koldyr.library.dto

/**
 * Description of class CredentialsDTO
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-09
 */
class CredentialsDTO {
    var username: String? = null
    var password: String? = null

    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
    }

    override fun toString(): String {
        return "CredentialsDTO(username=$username)"
    }
}
