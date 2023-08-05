package com.koldyr.library.model

import java.time.LocalDateTime
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
 * Description of class Feedback
 * @created: 2021-10-06
 */
@Entity
@Table(name = "T_FEEDBACK")
@SequenceGenerator(name = "FeedbackIds", sequenceName = "SEQ_FEEDBACK", allocationSize = 1)
class Feedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FeedbackIds")
    @Column(name = "FEEDBACK_ID")
    var id: Int? = null

    @ManyToOne @JoinColumn(name = "READER_ID")
    var reader: Reader? = null

    @ManyToOne @JoinColumn(name = "BOOK_ID")
    var book: Book? = null

    var date: LocalDateTime = LocalDateTime.now()

    var text: String? = null

    var rate: Int = 0
}
