package com.koldyr.library.dto

import com.koldyr.library.model.Reader
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class ReaderDetails(reader: Reader, private val authorities: Set<GrantedPrivilege>) : UserDetails {

    private var id: Int
    private var name: String
    private var password: String

    init {
        this.id = reader.id!!
        this.name = reader.mail
        this.password = reader.password
    }

    fun getReaderId(): Int {
        return id
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return name
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

    fun hasAuthority(authority: String): Boolean {
        return authorities.any { it.authority == authority }
    }

    fun hasRole(role: String): Boolean {
        return authorities.any { it.role == role }
    }
}
