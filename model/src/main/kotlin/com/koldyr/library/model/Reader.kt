package com.koldyr.library.model

import jakarta.persistence.Basic
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table


/**
 * Description of class Reader
 *
 * @created: 2019-10-25
 */
@Entity
@Table(name = "T_READER")
@SequenceGenerator(name = "ReaderIds", sequenceName = "SEQ_READER", allocationSize = 1)
class Reader : Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ReaderIds")
    @Column(name = "READER_ID")
    var id: Int? = null

    var firstName: String? = null

    var lastName: String? = null

    @Column(nullable = false, unique = true)
    var mail: String = ""

    var address: String? = null

    var phoneNumber: String? = null

    var note: String? = null

    @Basic(optional = false)
    var password: String = ""

    @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER)
    @JoinTable(
            name = "T_READER_ROLES",
            joinColumns = [JoinColumn(name = "reader_id", referencedColumnName = "reader_id")],
            inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Reader) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id!!
    }

    public override fun clone(): Reader {
        return super.clone() as Reader;
    }

    override fun toString(): String {
        return "Reader(id=$id, mail='$mail')"
    }
}
