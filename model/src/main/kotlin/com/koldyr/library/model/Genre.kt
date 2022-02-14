package com.koldyr.library.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_GENRE")
data class Genre (
    @Id
    @Column(name = "GENRE_ID")
    var id: Int? = null,

    @Column(name = "GENRE_NAME", nullable = false, unique = true)
    var name: String? = null
)
