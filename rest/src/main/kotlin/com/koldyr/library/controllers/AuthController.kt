package com.koldyr.library.controllers

import java.net.URI
import com.koldyr.library.dto.CredentialsDTO
import com.koldyr.library.dto.ReaderDTO
import com.koldyr.library.services.AuthenticationService
import com.koldyr.library.services.ReaderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Description of class AuthController
 * @created: 2022-02-09
 */
@Tag(name = "Authentication", description = "Users system authentication")
@ApiResponse(responseCode = "500", description = "Internal error occurred")
@RestController
class AuthController(
    private val readerService: ReaderService,
    private val authenticationService: AuthenticationService
) {

    @Operation(
        summary = "Creates a user", responses = [
            ApiResponse(responseCode = "201", description = "User created"),
            ApiResponse(responseCode = "400", description = "Wrong data for user")]
    )
    @PostMapping(path = ["/library/registration"], consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody reader: ReaderDTO): ResponseEntity<Void> {
        val readerId = readerService.create(reader)
        return created(URI.create("/library/readers/$readerId")).build()
    }

    @PostMapping(path = ["/library/login"], consumes = [APPLICATION_JSON_VALUE])
    fun login(@RequestBody credentials: CredentialsDTO): ResponseEntity<Unit> {
        val token = authenticationService.login(credentials)
        return ok().header(AUTHORIZATION, token).build()
    }
}
