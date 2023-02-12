package com.koldyr.library.services

import org.springframework.security.core.context.SecurityContextHolder
import ma.glasnost.orika.MapperFacade
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.ReaderDetails
import com.koldyr.library.model.Order
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.BookRepository

/**
 * Description of class BaseLibraryService
 *
 * @author: d.halitski@gmail.com
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

    protected fun getLoggedUser(): Reader = getReaderDetails().reader

    protected fun hasAuthority(authority: String): Boolean = getReaderDetails().hasAuthority(authority)

    protected fun hasRole(role: String): Boolean = getReaderDetails().hasRole(role)

    protected fun getReaderDetails(): ReaderDetails {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.principal as ReaderDetails
    }
}
