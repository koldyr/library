package com.koldyr.library.model

import java.time.LocalDate
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * Description of class Author
 * @created: 2021-10-06
 */
@Entity
@Table(name = "T_AUTHOR")
@SequenceGenerator(name = "AuthorIds", sequenceName = "SEQ_AUTHOR", allocationSize = 1)
class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AuthorIds")
    @Column(name = "AUTHOR_ID")
    var id: Int? = null

    var firstName: String? = null

    var lastName: String? = null

    @Column(columnDefinition = "DATE")
    var dateOfBirth: LocalDate? = null

    @OneToMany(mappedBy = "author", cascade = [CascadeType.PERSIST])
    var books: MutableSet<Book> = mutableSetOf()
}
