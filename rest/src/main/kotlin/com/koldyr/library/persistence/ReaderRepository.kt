package com.koldyr.library.persistence

import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Order
import com.koldyr.library.model.Reader
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Description of class ReaderRepository
 * @created: 2021-09-25
 */
@Repository("readerRepository")
interface ReaderRepository : JpaRepository<Reader, Int> {
    @Query("from Order where reader.id = :readerId")
    fun findOrders(@Param("readerId") readerId: Int): Collection<Order>
    
    @Query("from Feedback where reader.id = :readerId")
    fun findFeedbacks(readerId: Int): Collection<Feedback>
}
