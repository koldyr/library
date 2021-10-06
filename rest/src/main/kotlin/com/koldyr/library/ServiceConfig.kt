package com.koldyr.library

import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.ReaderRepository
import com.koldyr.library.services.BookService
import com.koldyr.library.services.BookServiceImpl
import com.koldyr.library.services.ReaderService
import com.koldyr.library.services.ReaderServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Description of class ServiceConfig
 * @created: 2021-09-28
 */
@Configuration
open class ServiceConfig {

    @Bean
    open fun readerService(personRepository: ReaderRepository): ReaderService {
        return ReaderServiceImpl(personRepository)
    }

    @Bean
    open fun bookService(bookRepository: BookRepository, authorRepository: AuthorRepository): BookService {
        return BookServiceImpl(bookRepository, authorRepository)
    }
}
