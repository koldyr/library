package com.koldyr.library.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import com.koldyr.library.model.Order

/**
 * Description of class OrderRepository
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-07
 */
@Repository("orderRepository")
interface OrderRepository : JpaRepository<Order, Int> {

    @Query("select case when count(o) > 0 then true else false end from Order o where o.bookId = :bookId")
    fun hasOrders(@Param("bookId") bookId: Int): Boolean

    fun findOrdersByBookId(bookId: Int): Collection<Order>
}
