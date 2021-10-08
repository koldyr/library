package com.koldyr.library.mapper

import com.koldyr.library.model.Order
import com.koldyr.library.persistence.OrderRepository
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.converter.BidirectionalConverter
import ma.glasnost.orika.metadata.Type
import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException
import java.util.Objects.*

/**
 * Description of class OrderConverter
 * @created: 2021-10-08
 */
class OrderConverter(private val orderRepository: OrderRepository) : BidirectionalConverter<Int, Order>() {

    override fun convertTo(orderId: Int?, type: Type<Order>?, context: MappingContext?): Order? {
        if (isNull(orderId)) {
            return null
        }
        return orderRepository.findById(orderId!!)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Reader with id '$orderId' is not found") }
    }

    override fun convertFrom(order: Order?, type: Type<Int>?, context: MappingContext?): Int? {
        return order?.id
    }
}
