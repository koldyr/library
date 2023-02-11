package com.koldyr.library.services

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import ma.glasnost.orika.MapperFacade
import com.koldyr.library.dto.CredentialsDTO
import com.koldyr.library.dto.ReaderDTO
import com.koldyr.library.dto.ReaderDetails

@Service("authenticationService")
class AuthenticationServiceImpl(
    @Value("\${spring.security.token.exp}") private val expiration: String,
    @Value("\${spring.security.secret}") secret: String,
    private val authenticationManager: AuthenticationManager,
    private val mapper: MapperFacade
) : AuthenticationService {

    private val algorithm: Algorithm = Algorithm.HMAC512(secret.toByteArray())
    
    override fun currentUser(): ReaderDTO {
        val reader = SecurityContextHolder.getContext().authentication.principal as ReaderDetails
        return mapper.map(reader, ReaderDTO::class.java)
    }

    override fun login(login: CredentialsDTO): String {
        try {
            val token = UsernamePasswordAuthenticationToken(login.username, login.password, listOf())
            val authentication = authenticationManager.authenticate(token)

            return "Bearer " + generateToken(authentication)
        } catch (e: DisabledException) {
            throw ResponseStatusException(UNAUTHORIZED, "user is disabled")
        } catch (e: BadCredentialsException) {
            throw ResponseStatusException(UNAUTHORIZED, "username or password invalid")
        }
    }

    private fun generateToken(authentication: Authentication): String {
        val tokenLive = LocalDateTime.now().plusMinutes(expiration.toLong())
        val expiration = Date.from(tokenLive.atZone(ZoneId.systemDefault()).toInstant())
        
        val user = authentication.principal as ReaderDetails
        val roles = user.reader.roles.map { it.name }.toTypedArray()

        return JWT.create()
            .withSubject(user.username)
            .withExpiresAt(expiration)
            .withArrayClaim("role", roles)
            .sign(algorithm)
    }
}
