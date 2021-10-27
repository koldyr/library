package com.koldyr.library.services

import java.util.Objects.isNull
import java.util.Objects.nonNull
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Order
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.AuthorityRepository
import com.koldyr.library.persistence.ReaderRepository
import ma.glasnost.orika.MapperFacade
import org.apache.commons.lang3.StringUtils.isEmpty
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class ReaderServiceImpl
 * @created: 2021-09-28
 */
open class ReaderServiceImpl(
        private val readerRepository: ReaderRepository,
        private val authorityRepository: AuthorityRepository,
        private val mapper: MapperFacade,
        private val encoder: PasswordEncoder
) : ReaderService {

    override fun findAll(): List<Reader> = readerRepository.findAll()

    @Transactional
    override fun create(reader: Reader): Int {
        if (isEmpty(reader.password)) {
            throw ResponseStatusException(BAD_REQUEST, "Reader password must be provided")
        }

        if (readerRepository.findByMail(reader.mail).isPresent) {
            throw ResponseStatusException(BAD_REQUEST, "Reader with mail '${reader.mail}' already exists")
        }

        reader.id = null
        if (reader.authorities.isEmpty()) {
            reader.authorities.add(authorityRepository.getById(0))
        }

        reader.password = encoder.encode(reader.password)
        val saved = readerRepository.save(reader)
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
        reader.password = persisted.password
        mapper.map(reader, persisted)

        readerRepository.save(persisted);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('librarian', 'supervisor')")
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
