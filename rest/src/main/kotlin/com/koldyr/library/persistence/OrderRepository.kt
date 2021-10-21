package com.koldyr.library.persistence

import com.koldyr.library.model.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Description of class OrderRepository
 * @created: 2021-10-07
 */
@Repository("orderRepository")
interface OrderRepository : JpaRepository<Order, Int> {

    @Query("select case when count(o) > 0 then true else false end from Order o where o.bookId = :bookId")
    fun hasOrders(@Param("bookId") bookId: Int): Boolean
}
