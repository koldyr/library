package com.koldyr.library.persistence

import com.koldyr.library.model.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Description of class OrderRepository
 * @created: 2021-10-07
 */
@Repository("orderRepository")
interface OrderRepository : JpaRepository<Order, Int> 