package com.koldyr.library.services

import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.ReaderDTO
import com.koldyr.library.model.Reader

/**
 * Description of class ReaderService
 * @created: 2021-09-28
 */
interface ReaderService {
    fun create(reader: Reader): Int
    fun findAll(): List<ReaderDTO>
    fun findById(readeId: Int): ReaderDTO
    fun update(readeId: Int, reader: Reader)
    fun delete(readerId: Int)

    fun findOrders(readerId: Int, returned: Boolean?): Collection<OrderDTO>
    fun findFeedbacks(readerId: Int): Collection<FeedbackDTO>
}
