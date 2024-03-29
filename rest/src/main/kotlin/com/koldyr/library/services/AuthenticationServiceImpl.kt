package com.koldyr.library.services

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import com.koldyr.library.dto.GrantedPrivilege

/**
 * Description of class AuthenticationServiceImpl
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-12
 */
@Service("authenticationService")
class AuthenticationServiceImpl(
    @Value("\${spring.security.token.exp}") val expiration: String,
    @Value("\${spring.security.secret}") secret: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.jws-algorithms}") algorithm: String,
    private val authenticationManager: AuthenticationManager,
) : AuthenticationService {

    private val signer = MACSigner(secret.toByteArray())

    private val header = JWSHeader.Builder(JWSAlgorithm.parse(algorithm)).contentType("text/plain").build()
    
    override fun login(login: String): String {
        try {
            val (userName, password) = getCredentials(login)
            val unauthenticatedToken = unauthenticated(userName, password)
            val authentication = authenticationManager.authenticate(unauthenticatedToken)

            return "Bearer " + generateToken(authentication)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        } catch (e: AuthenticationException) {
            throw ResponseStatusException(UNAUTHORIZED, e.message)
        }
    }

    private fun getCredentials(credentials: String): Pair<String, String> {
        if (!credentials.contains("Basic")) {
            throw IllegalArgumentException("Wrong authentication schema")
        }
        var userNamePassword = credentials.substringAfter("Basic ")
        userNamePassword = String(Base64.getDecoder().decode(userNamePassword), Charsets.UTF_8)
        val userName = userNamePassword.substringBefore(":")
        val password = userNamePassword.substringAfter(":")
        return Pair(userName, password)
    }

    private fun generateToken(authentication: Authentication): String {
        val tokenLive = LocalDateTime.now().plusMinutes(expiration.toLong())
        val expiration = Date.from(tokenLive.atZone(ZoneId.systemDefault()).toInstant())
        val roles = authentication.authorities
            .map { it as GrantedPrivilege }
            .map { it.role + ":" + it.privilege }

        val claimsSet = JWTClaimsSet.Builder()
            .subject(authentication.name)
            .expirationTime(expiration)
            .claim("scope", roles)
            .build()

        val jwt = SignedJWT(header, claimsSet)
        jwt.sign(signer)
        return jwt.serialize()
    }
}
