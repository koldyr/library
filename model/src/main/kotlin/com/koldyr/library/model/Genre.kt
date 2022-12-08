package com.koldyr.library.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_GENRE")
data class Genre (
    @Id
    @Column(name = "GENRE_ID")
    var id: Int? = null,

    @Column(name = "GENRE_NAME", nullable = false, unique = true)
    var name: String? = null
)
