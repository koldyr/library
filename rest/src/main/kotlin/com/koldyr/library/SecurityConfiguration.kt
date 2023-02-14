package com.koldyr.library

import javax.security.auth.kerberos.EncryptionKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.http.SessionCreationPolicy.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

/**
 * Description of class SecurityConfiguration
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-09
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration {


    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager = authenticationConfiguration.authenticationManager

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun grantedAuthorityDefaults(): GrantedAuthorityDefaults = GrantedAuthorityDefaults("")

    @Bean
    fun jwtDecoder(
        @Value("\${spring.security.secret}") secret: String,
        @Value("\${spring.security.oauth2.resourceserver.jwt.jws-algorithms}") algorithm: String
    ): JwtDecoder {
        return NimbusJwtDecoder
            .withSecretKey(EncryptionKey(secret.toByteArray(), 1))
            .macAlgorithm(MacAlgorithm.from(algorithm))
            .build()
    }

    @Bean
    fun authenticationConverter(): JwtAuthenticationConverter = JwtAuthenticationConverter()
        .also {
            val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
            grantedAuthoritiesConverter.setAuthorityPrefix("")
            it.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        }


    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .headers().disable()
            .csrf().disable()
            .cors()
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs.**", "/v3/api-docs/**", "/error/**", "/favicon.ico").permitAll()
            .requestMatchers(POST, "/library/login", "/library/registration").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2ResourceServer { it.jwt() }
        return http.build()
    }
}
