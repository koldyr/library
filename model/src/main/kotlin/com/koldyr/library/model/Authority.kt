package com.koldyr.library.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_AUTHORITY")
class Authority {

    @Id
    @Column(name = "AUTHORITY_ID")
    var id: Int? = null

    @ManyToOne @JoinColumn(name = "READER_ID")
    var reader: Reader? = null

    @Column(name = "GRANTED")
    var value: String = "READER"
}
