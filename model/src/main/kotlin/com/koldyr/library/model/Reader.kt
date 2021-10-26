package com.koldyr.library.model

import javax.persistence.Basic
import javax.persistence.CascadeType.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.*
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table

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
    @GeneratedValue(strategy = SEQUENCE, generator = "ReaderIds")
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

    @ManyToMany(targetEntity = Authority::class, cascade = [PERSIST, REMOVE], fetch = EAGER)
    @JoinTable(
            name = "T_READER_AUTHORITIES",
            joinColumns = [JoinColumn(name = "reader_id", referencedColumnName = "reader_id")],
            inverseJoinColumns = [JoinColumn(name = "authority_id", referencedColumnName = "authority_id")]
    )
    var authorities: MutableSet<Authority> = mutableSetOf()

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
}
