package com.koldyr.library.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_PRIVILEGE")
data class Privilege(
        @Id
        @Column(name = "PRIVILEGE_ID")
        var id: Int? = null,

        @Column(name = "PRIVILEGE_NAME", nullable = false, unique = true)
        var value: String = "read_book"
)
