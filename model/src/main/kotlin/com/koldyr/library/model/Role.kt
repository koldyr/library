package com.koldyr.library.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "T_ROLE")
class Role {
    @Id
    @Column(name = "ROLE_ID")
    var id: Int? = null

    @Column(name = "ROLE_NAME", nullable = false, unique = true)
    var name: String = "reader"

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "T_ROLE_PRIVILEGES",
        joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "ROLE_ID")],
        inverseJoinColumns = [JoinColumn(name = "privilege_id", referencedColumnName = "PRIVILEGE_ID")]
    )
    var privileges: Set<Privilege> = setOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Role) return false

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Role($name)"
    }
}
