package com.koldyr.library

import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.mapper.AuthorConverter
import com.koldyr.library.mapper.BookConverter
import com.koldyr.library.mapper.OrderConverter
import com.koldyr.library.mapper.ReaderConverter
import com.koldyr.library.model.Author
import com.koldyr.library.model.Book
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Order
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.FeedbackRepository
import com.koldyr.library.persistence.OrderRepository
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
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.VendorExtension
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/**
 * Description of class LibraryConfig
 * @created: 2021-09-28
 */
@Configuration
open class LibraryConfig {

    @Autowired
    lateinit var readerRepository: ReaderRepository

    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var authorRepository: AuthorRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var feedbackRepository: FeedbackRepository

    @Bean
    open fun readerService(mapper: MapperFacade): ReaderService {
        return ReaderServiceImpl(readerRepository, mapper)
    }

    @Bean
    open fun bookService(mapper: MapperFacade): BookService {
        return BookServiceImpl(bookRepository, authorRepository, readerRepository, orderRepository, feedbackRepository, mapper)
    }

    @Bean
    open fun authorService(mapper: MapperFacade): AuthorService {
        return AuthorServiceImpl(authorRepository, mapper)
    }

    @Bean
    open fun mapper(): MapperFacade {
        val mapperFactory: MapperFactory = DefaultMapperFactory.Builder().build()

        mapperFactory.classMap(Order::class.java, OrderDTO::class.java)
                .field("reader", "readerId")
                .byDefault()
                .register()
        mapperFactory.classMap(Book::class.java, BookDTO::class.java)
                .field("author", "authorId")
                .byDefault()
                .register()
        mapperFactory.classMap(Feedback::class.java, FeedbackDTO::class.java)
                .field("book", "bookId")
                .field("reader", "readerId")
                .byDefault()
                .register()
        mapperFactory.classMap(Author::class.java, AuthorDTO::class.java)
                .byDefault()
                .register()

        val converterFactory = mapperFactory.converterFactory
        converterFactory.registerConverter(BookConverter(bookRepository))
        converterFactory.registerConverter(AuthorConverter(authorRepository))
        converterFactory.registerConverter(ReaderConverter(readerRepository))
        converterFactory.registerConverter(OrderConverter(orderRepository))

        return mapperFactory.mapperFacade
    }

    @Bean
    open fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods(HttpMethod.GET.name, HttpMethod.HEAD.name, HttpMethod.POST.name, HttpMethod.PUT.name, HttpMethod.DELETE.name)
            }
        }
    }

    @Bean
    open fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/library/**"))
                .build()
    }

    private fun apiInfo(): ApiInfo {
        val title = "Library"
        val description = "RESTfull back end for Library SPA"
        val vendorExtensions: List<VendorExtension<*>> = mutableListOf()
        val termsOfServiceUrl = "http://koldyr.com/library/tos"
        val licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0"
        return ApiInfo(title, description, "1.0", termsOfServiceUrl, null, "Apache 2.0", licenseUrl, vendorExtensions)
    }

}

