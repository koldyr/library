package com.koldyr.library.security

import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class SecurityFilter
 * @created: 2022-02-09
 */
open class SecurityFilter(
    secret: String,
    authenticationManager: AuthenticationManager,
    private val readerDetailsService: UserDetailsService
) : BasicAuthenticationFilter(authenticationManager) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

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
        } catch (ex: ResponseStatusException) {
            throw ex
        } catch (ex: Exception) {
            if (ex.cause is ResponseStatusException) {
                throw ex.cause as ResponseStatusException
            }
            logger.error(ex.message, ex)
            response.sendError(UNAUTHORIZED.value(), ex.toString())
        }
    }

    private fun getAuthentication(token: String): Authentication {
        val jwt = jwtVerifier.verify(token.replace("Bearer ", ""))
        val username = jwt.subject

        val userDetails = readerDetailsService.loadUserByUsername(username)
        return LibraryAuthentication(userDetails)
    }
}
