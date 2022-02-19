package com.koldyr.library.services

import java.util.Objects.isNull
import java.util.Objects.nonNull
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.ReaderDTO
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.ReaderRepository
import com.koldyr.library.persistence.RoleRepository
import ma.glasnost.orika.MapperFacade
import org.apache.commons.lang3.StringUtils.isEmpty
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class ReaderServiceImpl
 * @created: 2021-09-28
 */
@Service
@Transactional
open class ReaderServiceImpl(
    bookRepository: BookRepository,
    mapper: MapperFacade,
    private val readerRepository: ReaderRepository,
    private val roleRepository: RoleRepository,
    private val encoder: PasswordEncoder
) : ReaderService, BaseLibraryService(bookRepository, mapper) {

    @PreAuthorize("hasAuthority('read_reader')")
    override fun findAll(): List<ReaderDTO> = readerRepository.findAll().map(this::mapReader)

    override fun create(reader: ReaderDTO): Int {
        if (isEmpty(reader.password)) {
            throw ResponseStatusException(BAD_REQUEST, "Reader password must be provided")
        }
        if (isEmpty(reader.mail)) {
            throw ResponseStatusException(BAD_REQUEST, "Reader email must be provided")
        }

        if (readerRepository.findByMail(reader.mail!!).isPresent) {
            throw ResponseStatusException(BAD_REQUEST, "Reader with mail '${reader.mail}' already exists")
        }

        reader.id = null
        val entity = mapper.map(reader, Reader::class.java)

        if (CollectionUtils.isEmpty(entity.roles)) {
            entity.roles.add(roleRepository.findByName("reader").get())
        }
        entity.password = encoder.encode(reader.password)

        val saved = readerRepository.save(entity)
        return saved.id!!
    }

    @PreAuthorize("hasAuthority('read_reader')")
    override fun findById(readeId: Int): ReaderDTO {
        return readerRepository.findById(readeId)
            .map(this::mapReader)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Reader with id '$readeId' is not found") }
    }

    @PreAuthorize("hasAuthority('modify_reader')")
    override fun update(readeId: Int, reader: ReaderDTO) {
        val persisted = readerRepository.findById(readeId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Reader with id '$readeId' is not found") }

        val roles = persisted.roles
        reader.id = persisted.id
        reader.password = persisted.password
        mapper.map(reader, persisted)

        if (hasRole("supervisor") && reader.roles.size > 0) {
            roles.clear()
            persisted.roles.forEach { roles.add(it) }
        }
        persisted.roles = roles

        readerRepository.save(persisted);
    }

    @PreAuthorize("hasAuthority('modify_reader')")
    override fun delete(readerId: Int) = readerRepository.deleteById(readerId)

    override fun currentReader(): ReaderDTO {
        return findById(getLoggedUserId())
    }

    @PreAuthorize("hasAuthority('read_order')")
    override fun findOrders(readerId: Int, returned: Boolean?): Collection<OrderDTO> {
        val orders = readerRepository.findOrders(readerId)

        if (isNull(returned)) {
            return orders.map(this::mapOrder)
        }

        return orders
            .filter { if (returned!!) nonNull(it.returned) else isNull(it.returned) }
            .map(this::mapOrder)
    }

    @PreAuthorize("hasAuthority('read_feedback')")
    override fun findFeedbacks(readerId: Int): Collection<FeedbackDTO> {
        return readerRepository.findFeedbacks(readerId).map(this::mapFeedBack)
    }

    private fun mapFeedBack(entity: Feedback): FeedbackDTO {
        return mapper.map(entity, FeedbackDTO::class.java)
    }

    private fun mapReader(entity: Reader): ReaderDTO {
        return mapper.map(entity, ReaderDTO::class.java)
    }
}
