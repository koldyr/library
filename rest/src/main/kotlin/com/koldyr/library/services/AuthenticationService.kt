package com.koldyr.library.services

/**
 * Description of class AuthenticationService
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-09
 */
interface AuthenticationService {
    fun login(login: String): String
}
