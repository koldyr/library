package com.koldyr.library

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.*
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import com.koldyr.library.security.SecurityFilter

/**
 * Description of class SecurityConfiguration
 * @created: 2022-02-09
 */
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration {

    @Value("\${spring.security.secret}")
    lateinit var secret: String

    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun securityFilter(authenticationManager: AuthenticationManager, readerDetailsService: UserDetailsService): SecurityFilter {
        return SecurityFilter(secret, authenticationManager, readerDetailsService)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity, securityFilter: SecurityFilter): SecurityFilterChain {
        http
            .headers().disable()
            .csrf().disable()
            .cors()
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/swagger-ui/*", "/v3/api-docs", "/v3/api-docs/*").permitAll()
            .requestMatchers(POST, "/library/login", "/library/registration").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(securityFilter)
            .sessionManagement().sessionCreationPolicy(STATELESS)
        return http.build()
    }
}
