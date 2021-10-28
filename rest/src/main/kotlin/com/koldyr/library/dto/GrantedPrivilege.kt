package com.koldyr.library.dto

import org.springframework.security.core.GrantedAuthority

/**
 * Description of class GrantedPrivilege
 * @created: 2021-10-28
 */
class GrantedPrivilege(
    val role: String,
    val privilege: String
) : GrantedAuthority {

    override fun getAuthority(): String {
        return privilege
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GrantedPrivilege

        if (privilege != other.privilege) return false

        return true
    }

    override fun hashCode(): Int {
        return privilege.hashCode()
    }

    override fun toString(): String {
        return "GrantedPrivilege(role='$role', privilege='$privilege')"
    }
}