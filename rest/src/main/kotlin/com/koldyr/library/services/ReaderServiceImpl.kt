package com.koldyr.library.services

import com.koldyr.library.model.Order
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.ReaderRepository
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class ReaderServiceImpl
 * @created: 2021-09-28
 */
open class ReaderServiceImpl(
    private val readerRepository: ReaderRepository
) : ReaderService {

    override fun findAll(): List<Reader> = readerRepository.findAll()
    
    @Transactional
    override fun create(person: Reader): Int {
        val saved = readerRepository.save(person)
        return saved.id!!
    }

    override fun findById(personId: Int): Reader {
        return readerRepository.findById(personId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Reader with id '$personId' is not found") }
    }

    @Transactional
    override fun update(readeId: Int, reader: Reader) {
        val persisted: Reader = readerRepository.findById(readeId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Reader with id '$readeId' is not found") }

        persisted.firstName = reader.firstName
        persisted.lastName = reader.lastName
        persisted.address = reader.address
        persisted.mail = reader.mail
        persisted.phoneNumber = reader.phoneNumber
        persisted.note = reader.note

        readerRepository.save(persisted);
    }

    @Transactional
    override fun delete(readerId: Int) = readerRepository.deleteById(readerId)

    override fun findOrders(readerId: Int): Collection<Order> {
        return readerRepository.findOrders(readerId)
    }
}
