package com.koldyr.library

import com.koldyr.library.security.SecurityFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.POST
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Description of class SecurityConfiguration
 * @created: 2022-02-09
 */
@EnableWebSecurity
@Configuration
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Value("\${spring.security.secret}")
    lateinit var secret: String

    @Autowired
    lateinit var readerDetailsService: UserDetailsService

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/api/library/login", "/api/library/registration")
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService(readerDetailsService)
            .passwordEncoder(passwordEncoder())
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .headers().disable()
            .csrf().disable()
            .cors()
            .and()
            .authorizeRequests()
            .antMatchers(POST, "/api/library/login", "/api/library/registration").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(securityFilter())
            .sessionManagement().sessionCreationPolicy(STATELESS)
    }

    @Bean
    @Throws(java.lang.Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun securityFilter(): SecurityFilter {
        return SecurityFilter(secret, authenticationManagerBean(), readerDetailsService)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
