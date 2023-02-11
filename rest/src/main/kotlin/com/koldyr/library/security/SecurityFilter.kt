package com.koldyr.library.security

import java.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

/**
 * Description of class SecurityFilter
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-09
 */
open class SecurityFilter(
    secret: String,
    authenticationManager: AuthenticationManager,
    private val readerDetailsService: UserDetailsService
) : BasicAuthenticationFilter(authenticationManager) {

    private val jwtVerifier: JWTVerifier

    init {
        val algorithm = Algorithm.HMAC512(secret.toByteArray())
        jwtVerifier = JWT.require(algorithm).build()
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        request.getHeader(AUTHORIZATION)?.let {
            val authentication = getAuthentication(it)
            SecurityContextHolder.getContext().authentication = authentication
        }

        chain.doFilter(request, response)
    }

    private fun getAuthentication(token: String): Authentication {
        val jwt = jwtVerifier.verify(token.replace("Bearer ", ""))
        val username = jwt.subject

        val userDetails = readerDetailsService.loadUserByUsername(username)
        return LibraryAuthentication(userDetails)
    }
}
