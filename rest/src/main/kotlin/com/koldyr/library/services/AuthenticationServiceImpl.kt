package com.koldyr.library.services

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.persistence.EntityNotFoundException
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.koldyr.library.dto.CredentialsDTO
import com.koldyr.library.dto.ReaderDTO
import com.koldyr.library.persistence.ReaderRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service("authenticationService")
class AuthenticationServiceImpl(
    @Value("\${spring.security.token.exp}") private val expiration: String,
    @Value("\${spring.security.secret}") secret: String,
    private val authenticationManager: AuthenticationManager,
    private val readerRepository: ReaderRepository,
    private val mapper: MapperFacade
) : AuthenticationService {

    private val algorithm: Algorithm = Algorithm.HMAC512(secret.toByteArray())
    
    override fun currentUser(): ReaderDTO {
        val email: String = SecurityContextHolder.getContext().authentication.principal.toString()

        val user = readerRepository.findByMail(email).orElseThrow { EntityNotFoundException("No user with email $email found") }

        return mapper.map(user, ReaderDTO::class.java)
    }

    override fun login(login: CredentialsDTO): String {
        try {
            val token = UsernamePasswordAuthenticationToken(login.username, login.password, listOf())
            authenticationManager.authenticate(token)

            return "Bearer " + generateToken(login.username!!)
        } catch (e: DisabledException) {
            throw ResponseStatusException(UNAUTHORIZED, "user is disabled")
        } catch (e: BadCredentialsException) {
            throw ResponseStatusException(UNAUTHORIZED, "username or password invalid")
        }
    }

    private fun generateToken(username: String): String {
        val tokenLive = LocalDateTime.now().plusMinutes(expiration.toLong())
        return JWT.create()
            .withSubject(username)
            .withExpiresAt(Date.from(tokenLive.atZone(ZoneId.systemDefault()).toInstant()))
            .sign(algorithm)
    }
}
