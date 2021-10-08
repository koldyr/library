package com.koldyr.library.services

import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Order
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.ReaderRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.Objects.*

/**
 * Description of class ReaderServiceImpl
 * @created: 2021-09-28
 */
open class ReaderServiceImpl(
        private val readerRepository: ReaderRepository,
        private val mapper: MapperFacade) : ReaderService {

    override fun findAll(): List<Reader> = readerRepository.findAll()

    @Transactional
    override fun create(person: Reader): Int {
        val saved = readerRepository.save(person)
        return saved.id!!
    }

    override fun findById(readeId: Int): Reader {
        return readerRepository.findById(readeId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Reader with id '$readeId' is not found") }
    }

    @Transactional
    override fun update(readeId: Int, reader: Reader) {
        val persisted = readerRepository.findById(readeId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Reader with id '$readeId' is not found") }

        reader.id = persisted.id
        mapper.map(reader, persisted)

        readerRepository.save(persisted);
    }

    @Transactional
    override fun delete(readerId: Int) = readerRepository.deleteById(readerId)

    override fun findOrders(readerId: Int, returned: Boolean?): Collection<OrderDTO> {
        val orders = readerRepository.findOrders(readerId)

        if (isNull(returned)) {
            return orders.map(this::mapOrder)
        }

        return orders
                .filter { if (returned!!) nonNull(it.returned) else isNull(it.returned) }
                .map(this::mapOrder)
    }

    override fun findFeedbacks(readerId: Int): Collection<FeedbackDTO> {
        return readerRepository.findFeedbacks(readerId).map(this::mapFeedBack)
    }

    private fun mapOrder(entity: Order): OrderDTO {
        return mapper.map(entity, OrderDTO::class.java)
    }

    private fun mapFeedBack(entity: Feedback): FeedbackDTO {
        return mapper.map(entity, FeedbackDTO::class.java)
    }
}
