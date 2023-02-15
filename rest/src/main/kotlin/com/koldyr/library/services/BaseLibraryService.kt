package com.koldyr.library.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import ma.glasnost.orika.MapperFacade
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.GrantedPrivilege
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Order
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.ReaderRepository

/**
 * Description of class BaseLibraryService
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-11-30
 */
open class BaseLibraryService {
    
    @Autowired
    protected lateinit var bookRepository: BookRepository

    @Autowired
    protected lateinit var readerRepository: ReaderRepository

    @Autowired
    protected lateinit var mapper: MapperFacade

    protected fun mapOrder(entity: Order): OrderDTO {
        val dto = mapper.map(entity, OrderDTO::class.java)
        bookRepository.findById(dto.bookId!!).ifPresent {
            dto.book = mapper.map(it, BookDTO::class.java)
        }
        return dto
    }

    protected fun currentUser(): Reader {
        val authentication = SecurityContextHolder.getContext().authentication
        return readerRepository.findByMail(authentication.name)
                .orElseThrow { throw UsernameNotFoundException("User with name '${authentication.name}' is not found") }
    }

    protected fun hasAuthority(authority: String): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.authorities
            .map { it as GrantedPrivilege }
            .any { it.privilege == authority }
    }

    protected fun hasRole(role: String): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.authorities
            .map { it as GrantedPrivilege }
            .any { it.role == role }
    }
}
