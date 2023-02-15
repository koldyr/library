package com.koldyr.library.persistence

import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Order
import com.koldyr.library.model.Reader

/**
 * Description of class ReaderRepository
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-09-25
 */
@Repository("readerRepository")
interface ReaderRepository : JpaRepository<Reader, Int> {

    fun findByMail(email: String): Optional<Reader>

    @Query("from Order where reader.id = :readerId")
    fun findOrders(@Param("readerId") readerId: Int): Collection<Order>
    
    @Query("from Feedback where reader.id = :readerId")
    fun findFeedbacks(readerId: Int): Collection<Feedback>
}
