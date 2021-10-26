package com.koldyr.library.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_AUTHORITY")
data class Authority(

        @Id
        @Column(name = "AUTHORITY_ID")
        var id: Int? = null,

        @Column(name = "GRANTED", nullable = false)
        var value: String = "reader"
)
