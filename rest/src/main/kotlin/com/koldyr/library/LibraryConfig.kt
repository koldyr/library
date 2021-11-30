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
import com.koldyr.library.persistence.RoleRepository
import com.koldyr.library.services.AuthorService
import com.koldyr.library.services.AuthorServiceImpl
import com.koldyr.library.services.BookService
import com.koldyr.library.services.BookServiceImpl
import com.koldyr.library.services.ReaderService
import com.koldyr.library.services.ReaderServiceImpl
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
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Description of class LibraryConfig
 * @created: 2021-09-28
 */
@Configuration
@EnableAspectJAutoProxy
class LibraryConfig : WebSecurityConfigurerAdapter() {

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

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var readerDetailsService: UserDetailsService

    @Bean
    fun readerService(mapper: MapperFacade, encoder: PasswordEncoder): ReaderService {
        return ReaderServiceImpl(bookRepository, mapper, readerRepository, roleRepository, encoder)
    }

    @Bean
    fun bookService(mapper: MapperFacade): BookService {
        return BookServiceImpl(bookRepository, mapper, authorRepository, orderRepository, feedbackRepository)
    }

    @Bean
    fun authorService(mapper: MapperFacade): AuthorService {
        return AuthorServiceImpl(bookRepository, mapper, authorRepository)
    }

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

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService(readerDetailsService)
                .passwordEncoder(encoder())
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .antMatchers("/login*").permitAll()
                .antMatchers(POST, "/api/library/readers").permitAll()
                .anyRequest().authenticated()
                .and()
                .cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .formLogin()
                .successHandler { request, response, authentication -> }
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .and()
                .httpBasic()
                .and()
                .headers().disable()
                .csrf().disable()
    }

    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedMethods = listOf(GET.name, PUT.name, POST.name, DELETE.name)
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration.applyPermitDefaultValues())
        return source
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun api(): OpenAPI {
        return OpenAPI()
                .components(Components())
                .info(apiInfo())
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

