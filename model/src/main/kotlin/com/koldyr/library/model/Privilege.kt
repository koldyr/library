package com.koldyr.library.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_PRIVILEGE")
data class Privilege(
    @Id
    @Column(name = "PRIVILEGE_ID")
    var id: Int? = null,

    @Column(name = "PRIVILEGE_NAME", nullable = false, unique = true)
    var value: String = "read_book"
)
