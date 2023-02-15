package com.koldyr.library.services

import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.ReaderDTO

/**
 * Description of class ReaderService
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-09-28
 */
interface ReaderService {
    fun create(reader: ReaderDTO): Int
    fun findAll(): List<ReaderDTO>
    fun findById(readeId: Int): ReaderDTO
    fun update(readeId: Int, reader: ReaderDTO)
    fun delete(readerId: Int)
    fun currentReader(): ReaderDTO

    fun findOrders(readerId: Int, returned: Boolean?): Collection<OrderDTO>
    fun findFeedbacks(readerId: Int): Collection<FeedbackDTO>
}
