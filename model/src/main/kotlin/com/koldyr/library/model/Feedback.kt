package com.koldyr.library.model

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

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

    @OneToOne
    var reader: Reader? = null

    @OneToOne
    var book: Book? = null

    var date: LocalDateTime = LocalDateTime.now()

    var text: String? = null

    var rate: Int = 0
}
