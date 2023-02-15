package com.koldyr.library.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.koldyr.library.model.Feedback

/**
 * Description of class FeedbackRepository
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-07
 */
@Repository("feedbackRepository")
interface FeedbackRepository : JpaRepository<Feedback, Int>{
    fun findAllByBookId(bookId: Int): List<Feedback>
}
