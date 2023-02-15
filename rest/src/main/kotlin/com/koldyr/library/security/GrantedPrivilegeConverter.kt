package com.koldyr.library.security

import java.util.regex.Pattern
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.util.StringUtils
import com.koldyr.library.dto.GrantedPrivilege

/**
 * Description of the GrantedPrivilegeConverter class
 *
 * @author d.halitski@gmail.com
 * @created 2023-02-15
 */
class GrantedPrivilegeConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    private val logger = LoggerFactory.getLogger(GrantedPrivilegeConverter::class.java)

    private val wellKnownAuthoritiesClaimNames = mutableListOf("scope", "scp")

    private val pattern = Pattern.compile(":")

    override fun convert(source: Jwt): Collection<GrantedAuthority> {
        return getAuthorities(source)
            .map {
                val attributes = pattern.split(it)
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
            this.logger.trace("Returning no authorities since could not find any claims that might contain scopes")
            return emptyList()
        }
        
        this.logger.trace("Looking for scopes in claim {}", claimName)

        val authorities = jwt.getClaim<Any>(claimName)
        return if (authorities is String) {
            if (StringUtils.hasText(authorities)) {
                listOf(*authorities.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            } else {
                emptyList()
            }
        } else {
            (authorities as? Collection<*>)?.let { castAuthoritiesToCollection(it) } ?: emptyList()
        }
    }

    private fun castAuthoritiesToCollection(authorities: Any): Collection<String> {
        return authorities as Collection<String>
    }
}
