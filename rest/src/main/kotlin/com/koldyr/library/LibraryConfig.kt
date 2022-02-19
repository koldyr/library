package com.koldyr.library

import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.ReaderDTO
import com.koldyr.library.mapper.AuthorConverter
import com.koldyr.library.mapper.BookConverter
import com.koldyr.library.mapper.GenreConverter
import com.koldyr.library.mapper.OrderConverter
import com.koldyr.library.mapper.ReaderConverter
import com.koldyr.library.mapper.RoleConverter
import com.koldyr.library.model.Author
import com.koldyr.library.model.Book
import com.koldyr.library.model.Feedback
import com.koldyr.library.model.Order
import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.AuthorRepository
import com.koldyr.library.persistence.BookRepository
import com.koldyr.library.persistence.GenreRepository
import com.koldyr.library.persistence.OrderRepository
import com.koldyr.library.persistence.ReaderRepository
import com.koldyr.library.persistence.RoleRepository
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.MapperFactory
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.HEAD
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Description of class LibraryConfig
 * @created: 2021-09-28
 */
@Configuration
@EnableAspectJAutoProxy
class LibraryConfig {

    @Autowired
    lateinit var readerRepository: ReaderRepository

    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var authorRepository: AuthorRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var genreRepository: GenreRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Bean
    fun mapper(): MapperFacade {
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
        mapperFactory.classMap(Reader::class.java, ReaderDTO::class.java)
            .fieldBToA("password", "password")
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
        converterFactory.registerConverter(GenreConverter(genreRepository))
        converterFactory.registerConverter(RoleConverter(roleRepository))

        return mapperFactory.mapperFacade
    }

    @Bean
    fun api(): OpenAPI {
        return OpenAPI()
            .components(Components())
            .info(apiInfo())
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods(GET.name, HEAD.name, POST.name, PUT.name, DELETE.name, PATCH.name)
                    .exposedHeaders(AUTHORIZATION)
            }
        }
    }

    private fun apiInfo(): Info {
        val license = License()
            .name("Apache 2.0")
            .url("http://www.apache.org/licenses/LICENSE-2.0")
        return Info()
            .title("Library")
            .description("RESTfull back end for Library SPA")
            .termsOfService("http://koldyr.com/library/tos")
            .license(license)
    }
}

