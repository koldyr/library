package com.koldyr.library.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "T_ROLE")
class Role() {
    @Id
    @Column(name = "ROLE_ID")
    @JsonIgnore
    var id: Int? = null

    @Column(name = "ROLE_NAME", nullable = false, unique = true)
    var name: String = "reader"

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "T_ROLE_PRIVILEGES",
            joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "ROLE_ID")],
            inverseJoinColumns = [JoinColumn(name = "privilege_id", referencedColumnName = "PRIVILEGE_ID")])
    @JsonIgnore
    var privileges: Set<Privilege> = setOf()

    constructor(id: Int, name: String) : this() {
        this.id = id
        this.name = name
    }

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
