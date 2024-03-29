package com.koldyr.library.controllers

import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Size
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import com.koldyr.library.dto.ErrorResponse
import com.koldyr.library.dto.ReaderDTO
import com.koldyr.library.services.AuthenticationService
import com.koldyr.library.services.ReaderService

/**
 * Description of class AuthController
 *
 * @author: d.halitski@gmail.com
 * @created: 2022-02-09
 */
@RestController
@Tag(name = "Authentication", description = "Users system authentication")
class AuthController(
    private val readerService: ReaderService,
    private val authenticationService: AuthenticationService
) : BaseController() {

    @Operation(
        summary = "Creates a user", responses = [
            ApiResponse(responseCode = "201", description = "User created", content = [Content()], headers = [Header(name = LOCATION, description = "url to new reader")]),
            ApiResponse(
                responseCode = "400", description = "Wrong data for user",
                content = [Content(schema = Schema(implementation = ErrorResponse::class), mediaType = APPLICATION_JSON_VALUE)]
            )
        ]
    )
    @PostMapping(path = ["/api/v1/registration"], consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody reader: ReaderDTO): ResponseEntity<Void> {
        val readerId = readerService.create(reader)
        return created(URI.create("/api/v1/readers/$readerId")).build()
    }

    @Operation(
        summary = "Login reader in library", responses = [
            ApiResponse(
                responseCode = "200", description = "User logged in", content = [Content()],
                headers = [Header(name = AUTHORIZATION, description = "JWT access token for authentication")]
            ),
            ApiResponse(
                responseCode = "400", description = "Wrong login user",
                content = [Content(schema = Schema(implementation = ErrorResponse::class), mediaType = APPLICATION_JSON_VALUE)]
            )
        ]
    )
    @PostMapping(path = ["/api/v1/login"])
    fun login(@RequestHeader(AUTHORIZATION) @Size(max = 256) credentials: String): ResponseEntity<Unit> {
        val token = authenticationService.login(credentials)
        return ok().header(AUTHORIZATION, token).build()
    }
}
