package com.koldyr.library.controllers

import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import com.koldyr.library.dto.ErrorResponse
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.ReaderDTO
import com.koldyr.library.services.ReaderService

/**
 * Description of class ReaderController
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/library/readers")
@Tag(name = "ReaderController", description = "Reader operations")
@ApiResponse(
    responseCode = "500", description = "Internal error occurred",
    content = [Content(schema = Schema(implementation = ErrorResponse::class), mediaType = APPLICATION_JSON_VALUE)]
)
class ReaderController(private val readerService: ReaderService) {

    @Operation(
        summary = "Search for authors",
        responses = [ApiResponse(
            responseCode = "200", description = "List of all readers",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = ReaderDTO::class)))]
        )]
    )
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun readers(): Collection<ReaderDTO> = readerService.findAll()

    @Operation(
        summary = "Update reader attributes",
        responses = [ApiResponse(responseCode = "200", description = "Reader updated", content = [Content()])]
    )
    @PutMapping("/{readerId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable("readerId") readerId: Int, @RequestBody reader: ReaderDTO): ResponseEntity<Unit> {
        readerService.update(readerId, reader)
        return ok().build()
    }

    @Operation(
        summary = "Get reader by id",
        responses = [ApiResponse(
            responseCode = "200", description = "Reader data",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ReaderDTO::class))]
        )]
    )
    @GetMapping("/{readerId}", produces = [APPLICATION_JSON_VALUE])
    fun readerById(@PathVariable("readerId") readerId: Int): ReaderDTO = readerService.findById(readerId)

    @Operation(
        summary = "Get current reader",
        responses = [ApiResponse(
            responseCode = "200", description = "Reader data",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ReaderDTO::class))]
        )]
    )
    @GetMapping("/me", produces = [APPLICATION_JSON_VALUE])
    fun currentReader(): ReaderDTO = readerService.currentReader()

    @Operation(
        summary = "Delete reader with id",
        responses = [ApiResponse(responseCode = "204", description = "Reader deleted", content = [Content()])]
    )
    @DeleteMapping("/{readerId}")
    fun delete(@PathVariable("readerId") readerId: Int): ResponseEntity<Unit> {
        readerService.delete(readerId)

        return noContent().build()
    }

    @Operation(
        summary = "List of orders for passed reader",
        parameters = [Parameter(name = "returned", `in` = ParameterIn.QUERY)],
        responses = [ApiResponse(
            responseCode = "200", description = "List of orders for reader",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = OrderDTO::class)))]
        )]
    )
    @GetMapping("/{readerId}/orders", produces = [APPLICATION_JSON_VALUE])
    fun orders(@PathVariable("readerId") readerId: Int, @RequestParam(name = "returned", required = false) returned: Boolean?): Collection<OrderDTO> =
        readerService.findOrders(readerId, returned)

    @Operation(
        summary = "List of feedbacks for passed reader",
        responses = [ApiResponse(
            responseCode = "200", description = "List of feedbacks from reader",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = FeedbackDTO::class)))]
        )]
    )
    @GetMapping("/{readerId}/feedbacks", produces = [APPLICATION_JSON_VALUE])
    fun feedbacks(@PathVariable("readerId") readerId: Int): Collection<FeedbackDTO> = readerService.findFeedbacks(readerId)
}
