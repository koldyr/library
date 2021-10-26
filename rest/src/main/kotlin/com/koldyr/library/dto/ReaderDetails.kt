package com.koldyr.library.dto

import com.koldyr.library.model.Reader
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class ReaderDetails(private val reader: Reader) : UserDetails {

    fun getReader(): Reader {
        return reader
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return reader.authorities.map { SimpleGrantedAuthority(it.value) }.toMutableSet()
    }

    override fun getPassword(): String {
        return reader.password
    }

    override fun getUsername(): String {
        return reader.mail
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
