package com.koldyr.library.model

import java.time.LocalDate
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
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * Description of class Book
 * @created: 2019-10-25
 */
@Entity
@Table(name = "T_BOOK")
@SequenceGenerator(name = "BookIds", sequenceName = "SEQ_BOOK", allocationSize = 1)
class Book() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BookIds")
    @Column(name = "BOOK_ID")
    var id: Int? = null

    var title: String? = null

    var publishingHouse: String? = null

    @ManyToOne @JoinColumn(name = "AUTHOR_ID")
    var author: Author? = null

    @Column(columnDefinition = "DATE")
    var publicationDate: LocalDate? = null

    @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER)
    @JoinTable(
        name = "T_BOOK_GENRE",
        joinColumns = [JoinColumn(name = "book_id", referencedColumnName = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "genre_id", referencedColumnName = "genre_id")]
    )
    var genres: MutableSet<Genre> = mutableSetOf()

    var bookCover: String? = null

    var note: String? = null

    var count: Int = 0

    constructor(id: Int) : this() {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Book) return false

        if (id != other.id) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (title?.hashCode() ?: 0)
        return result
    }
}
