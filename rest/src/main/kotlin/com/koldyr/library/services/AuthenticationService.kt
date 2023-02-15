package com.koldyr.library.services

import com.koldyr.library.dto.CredentialsDTO

/**
 * Description of class AuthenticationService
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-09
 */
interface AuthenticationService {
    fun login(login: CredentialsDTO): String
}
