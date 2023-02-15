package com.koldyr.library.services

import com.koldyr.library.dto.CredentialsDTO
import com.koldyr.library.dto.ReaderDTO

/**
 * Description of class AuthenticationService
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-09
 */
interface AuthenticationService {
    fun currentUser(): ReaderDTO
    fun login(login: CredentialsDTO): String
}
