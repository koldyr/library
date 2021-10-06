package com.koldyr.library.model

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.*
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

/**
 * Description of class Book
 * @created: 2019-10-25
 */
@Entity
@Table(name = "T_BOOK")
@SequenceGenerator(name = "BookIds", sequenceName = "SEQ_BOOK", allocationSize = 1)
class Book() {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "BookIds")
    @Column(name = "BOOK_ID")
    var id: Int? = null

    var title: String? = null

    var publishingHouse: String? = null

    @ManyToOne @JoinColumn(name = "AUTHOR_ID")
    var author: Author? = null

    @Column(columnDefinition = "DATE")
    var publicationDate: LocalDate? = null

    @Enumerated(EnumType.STRING)
    var genre: Genre? = null

    var bookCover: String? = null

    var note: String? = null

    constructor(id: Int) : this() {
        this.id = id
    }
}
