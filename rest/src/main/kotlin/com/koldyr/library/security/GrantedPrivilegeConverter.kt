package com.koldyr.library.security

import java.util.regex.Pattern
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import com.koldyr.library.dto.GrantedPrivilege

/**
 * Description of the GrantedPrivilegeConverter class
 *
 * @author d.halitski@gmail.com
 * @created 2023-02-15
 */
class GrantedPrivilegeConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val wellKnownAuthoritiesClaimNames = mutableListOf("scope", "scp")

    private val roleDelimiter = Pattern.compile(":")
    
    private val authDelimiter = Pattern.compile(" ")

    override fun convert(source: Jwt): Collection<GrantedAuthority> {
        return getAuthorities(source)
            .map {
                val attributes = roleDelimiter.split(it)
                GrantedPrivilege(attributes.first(), attributes.last())
            }.toSet()
    }

    private fun getAuthoritiesClaimName(jwt: Jwt): String? {
        for (claimName in wellKnownAuthoritiesClaimNames) {
            if (jwt.hasClaim(claimName)) {
                return claimName
            }
        }
        return null
    }

    private fun getAuthorities(jwt: Jwt): Collection<String> {
        val claimName = getAuthoritiesClaimName(jwt)
        if (claimName == null) {
            logger.trace("Returning no authorities since could not find any claims that might contain scopes")
            return emptyList()
        }
        
        val authorities = jwt.getClaim<Any>(claimName)
        return if (authorities is String) {
            if (authorities.isBlank()) {
                emptyList()
            } else {
                authDelimiter.split(authorities).dropLastWhile { it.isEmpty() }.toList()
            }
        } else {
            if (authorities is Collection<*>) authorities as Collection<String> else emptyList()
        }
    }
}
