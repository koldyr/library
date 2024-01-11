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
import org.springframework.security.web.SecurityFilterChain
import com.koldyr.library.security.GrantedPrivilegeConverter

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
    ): JwtDecoder = NimbusJwtDecoder
        .withSecretKey(EncryptionKey(secret.toByteArray(), 1))
        .macAlgorithm(MacAlgorithm.from(algorithm))
        .build()

    @Bean
    fun authenticationConverter(): JwtAuthenticationConverter = JwtAuthenticationConverter()
        .also {
            it.setJwtGrantedAuthoritiesConverter(GrantedPrivilegeConverter())
        }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .headers { it.disable() }
            .csrf { it.disable() }
            .cors {}
            .authorizeHttpRequests {
                it.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs.**", "/v3/api-docs/**", "/error/**", "/favicon.ico").permitAll()
                    .requestMatchers(POST, "/api/v1/login", "/api/v1/registration").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt { } }
        return http.build()
    }
}
