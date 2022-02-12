package com.koldyr.library.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

/**
 * Description of class LibraryAuthentication
 * @created: 2022-02-11
 */
class LibraryAuthentication(
    private val principal: UserDetails
) : AbstractAuthenticationToken(principal.authorities) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): String {
        return principal.password
    }

    override fun getPrincipal(): UserDetails {
        return principal
    }
}