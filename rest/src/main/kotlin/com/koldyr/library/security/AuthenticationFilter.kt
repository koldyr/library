package com.koldyr.library.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Description of class AuthenticationFilter
 * @created: 2022-02-09
 */
open class AuthenticationFilter(
    secret: String,
    authenticationManager: AuthenticationManager
) : BasicAuthenticationFilter(authenticationManager) {

    private val jwtVerifier: JWTVerifier

    init {
        val algorithm = Algorithm.HMAC512(secret.toByteArray())
        jwtVerifier = JWT.require(algorithm).build()
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            val header: String? = request.getHeader(AUTHORIZATION)
            if (header == null || !header.startsWith("Bearer ")) {
                chain.doFilter(request, response)
                return
            }

            val authentication = getAuthentication(header)
            SecurityContextHolder.getContext().authentication = authentication
            chain.doFilter(request, response)
        } catch (ex: JWTVerificationException) {
            response.sendError(UNAUTHORIZED.value(), "invalid token")
        }
    }

    private fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
        val user = jwtVerifier
            .verify(token.replace("Bearer ", ""))
            .subject
        return UsernamePasswordAuthenticationToken(user, null, listOf())
    }
}
