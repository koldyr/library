package com.koldyr.library

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.mapper.AuthorConverter
import com.koldyr.library.mapper.BookConverter
import com.koldyr.library.mapper.ReaderConverter
import com.koldyr.library.model.Book
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Order
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.ReaderRepository
import com.koldyr.library.services.AuthorService
import com.koldyr.library.services.AuthorServiceImpl
import com.koldyr.library.services.BookService
import com.koldyr.library.services.BookServiceImpl
import com.koldyr.library.services.ReaderService
import com.koldyr.library.services.ReaderServiceImpl
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.MapperFactory
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Description of class ServiceConfig
 * @created: 2021-09-28
 */
@Configuration
open class ServiceConfig {

    @Autowired
    lateinit var readerRepository: ReaderRepository

    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var authorRepository: AuthorRepository

    @Bean
    open fun readerService(mapper: MapperFacade): ReaderService {
        return ReaderServiceImpl(readerRepository, mapper)
    }

    @Bean
    open fun bookService(mapper: MapperFacade): BookService {
        return BookServiceImpl(bookRepository, authorRepository, mapper)
    }

    @Bean
    open fun authorService(mapper: MapperFacade): AuthorService {
        return AuthorServiceImpl(authorRepository, mapper)
    }

    @Bean
    open fun mapper(): MapperFacade {
        val mapperFactory: MapperFactory = DefaultMapperFactory.Builder().build()

        mapperFactory.classMap(Order::class.java, OrderDTO::class.java)
                .field("reader.id", "readerId")
                .byDefault()
                .register()

        mapperFactory.classMap(Book::class.java, BookDTO::class.java)
                .field("author.id", "authorId")
                .byDefault()
                .register()

        mapperFactory.classMap(Feedback::class.java, FeedbackDTO::class.java)
                .field("reader.id", "readerId")
                .field("book.id", "bookId")
                .byDefault()
                .register()

        val converterFactory = mapperFactory.converterFactory
        converterFactory.registerConverter(BookConverter(bookRepository))
        converterFactory.registerConverter(AuthorConverter(authorRepository))
        converterFactory.registerConverter(ReaderConverter(readerRepository))

        return mapperFactory.mapperFacade
    }
}

