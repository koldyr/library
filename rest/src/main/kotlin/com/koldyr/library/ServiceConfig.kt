package com.koldyr.library

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Book
import com.koldyr.library.model.Order
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.ReaderRepository
import com.koldyr.library.services.BookService
import com.koldyr.library.services.BookServiceImpl
import com.koldyr.library.services.ReaderService
import com.koldyr.library.services.ReaderServiceImpl
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.MapperFactory
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * Description of class ServiceConfig
 * @created: 2021-09-28
 */
@Configuration
open class ServiceConfig {

    @Bean
    open fun readerService(personRepository: ReaderRepository, mapper: MapperFacade): ReaderService {
        return ReaderServiceImpl(personRepository, mapper)
    }

    @Bean
    open fun bookService(bookRepository: BookRepository, authorRepository: AuthorRepository): BookService {
        return BookServiceImpl(bookRepository, authorRepository)
    }

    @Bean
    open fun mapper(): MapperFacade {
        val mapperFactory: MapperFactory = DefaultMapperFactory.Builder().build()

        mapperFactory
            .classMap(Order::class.java, OrderDTO::class.java)
            .field("reader.id", "readerId")
            .byDefault()
            .register()

        mapperFactory
            .classMap(Book::class.java, BookDTO::class.java)
            .field("author.id", "authorId")
            .byDefault()
            .register()

        return mapperFactory.mapperFacade
    }
}
