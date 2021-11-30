package com.koldyr.library.services

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.ReaderDetails
import com.koldyr.library.model.Order
import com.koldyr.library.persistence.BookRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Description of class BaseLibraryService
 * @created: 2021-11-30
 */
open class BaseLibraryService(
    protected val bookRepository: BookRepository,
    protected val mapper: MapperFacade
) {
    protected fun mapOrder(entity: Order): OrderDTO {
        val dto = mapper.map(entity, OrderDTO::class.java)
        bookRepository.findById(dto.bookId!!).ifPresent {
            dto.book = mapper.map(it, BookDTO::class.java)
        }
        return dto
    }

    protected fun getLoggedUserId(): Int {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication
        val readerDetails = authentication.principal as ReaderDetails
        return readerDetails.getReaderId()
    }

    protected fun hasAuthority(authority: String): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication
        val readerDetails = authentication.principal as ReaderDetails
        return readerDetails.hasAuthority(authority)
    }
}
