package com.koldyr.library.persistence

import com.koldyr.library.model.Feedback
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Description of class FeedbackRepository
 * @created: 2021-10-07
 */
@Repository("feedbackRepository")
interface FeedbackRepository : JpaRepository<Feedback, Int>{
    fun findAllByBookId(bookId: Int): List<Feedback>
}
