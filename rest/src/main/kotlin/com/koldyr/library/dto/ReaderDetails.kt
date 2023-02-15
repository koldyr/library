package com.koldyr.library.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import com.koldyr.library.model.Reader

/**
 * Description of class ReaderDetails
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-28
 */
class ReaderDetails(val reader: Reader) : UserDetails {

    private val authorities: Set<GrantedPrivilege>

    init {
        val authorities = mutableSetOf<GrantedPrivilege>()
        for (role in reader.roles) {
            for (privilege in role.privileges) {
                authorities.add(GrantedPrivilege(role.name, privilege.value))
            }
        }
        this.authorities = authorities
    }

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String = reader.password

    override fun getUsername(): String = reader.mail

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
