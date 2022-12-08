package com.koldyr.library.model

import java.time.LocalDateTime
import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * Description of class Order
 * @created: 2019-10-26
 */
@Entity
@Table(name = "T_ORDER")
@SequenceGenerator(name = "OrderIds", sequenceName = "SEQ_ORDER", allocationSize = 1)
class Order : Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OrderIds")
    @Column(name = "ORDER_ID")
    var id: Int? = null

    @Basic(optional = false)
    var bookId: Int? = null

    @ManyToOne(optional = false) @JoinColumn(name = "READER_ID")
    var reader: Reader? = null

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    var ordered: LocalDateTime = LocalDateTime.now()

    @Column(columnDefinition = "TIMESTAMP")
    var returned: LocalDateTime? = null

    var notes: String? = null

    public override fun clone(): Order {
        return super.clone() as Order
    }
}
