package com.koldyr.library.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

/**
 * Description of class LibraryAuthentication
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-11
 */
class LibraryAuthentication(
    private val userDetails: UserDetails
) : AbstractAuthenticationToken(userDetails.authorities) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): String = userDetails.password

    override fun getPrincipal(): UserDetails = userDetails

    override fun getDetails(): UserDetails = userDetails
}
