package com.koldyr.library.controllers

import java.time.LocalDateTime.now
import javax.persistence.PersistenceException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.koldyr.library.dto.ErrorResponse
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException


/**
 * Description of class ErrorsHandler
 * @created: 2022-02-12
 */
@RestControllerAdvice
class ErrorsHandler {
    
    @ExceptionHandler(JWTVerificationException::class)
    @ResponseStatus(UNAUTHORIZED)
    fun handleAuthentication(error: JWTVerificationException): ErrorResponse? {
        return ErrorResponse(UNAUTHORIZED.value().toShort(), now(), error.message)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleGeneralError(error: ResponseStatusException): ErrorResponse? {
        return ErrorResponse(error.status.value().toShort(), now(), error.reason)
    }

    @ExceptionHandler(PersistenceException::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleGeneralError(error: PersistenceException): ErrorResponse? {
        return ErrorResponse(INTERNAL_SERVER_ERROR.value().toShort(), now(), error.message)
    }
}