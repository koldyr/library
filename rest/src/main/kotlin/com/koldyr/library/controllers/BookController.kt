package com.koldyr.library.controllers

import java.net.URI
import java.util.Objects.*
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.ErrorResponse
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.PageResultDTO
import com.koldyr.library.dto.SearchCriteria
import com.koldyr.library.services.BookService

/**
 * Description of class BookController
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/library/books")
@Tag(name = "BookController", description = "Book operations")
@ApiResponse(
    responseCode = "500", description = "Internal error occurred",
    content = [Content(schema = Schema(implementation = ErrorResponse::class), mediaType = APPLICATION_JSON_VALUE)]
)
class BookController(private val bookService: BookService) {

    @Operation(
        summary = "List of books for current user reader",
        responses = [ApiResponse(
            responseCode = "200", description = "List of orders available for order",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = BookDTO::class)))]
        )]
    )
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun books(@RequestParam(name = "available", required = false) available: Boolean): Collection<BookDTO> = bookService.findAll(available)

    @Operation(
        summary = "Look up for books by criteria",
        responses = [ApiResponse(
            responseCode = "200", description = "Page of books",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = PageResultDTO::class))]
        )]
    )
    @PostMapping("/search", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun searchBooks(@RequestBody(required = false) criteria: SearchCriteria?): PageResultDTO<BookDTO> = bookService.findBooks(criteria)

    @Operation(
        summary = "Create new book",
        responses = [ApiResponse(responseCode = "201", description = "Book created", content = [Content()])]
    )
    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody book: BookDTO): ResponseEntity<Unit> {
        val bookId = bookService.create(book)

        val uri = URI.create("/library/books/${bookId}")
        return created(uri).build()
    }

    @Operation(
        summary = "Get book by id",
        responses = [ApiResponse(
            responseCode = "200", description = "Book data",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = BookDTO::class))]
        )]
    )
    @GetMapping("/{bookId}", produces = [APPLICATION_JSON_VALUE])
    fun bookById(@PathVariable("bookId") bookId: Int): BookDTO = bookService.findById(bookId)

    @Operation(
        summary = "Update book attributes by id",
        responses = [ApiResponse(responseCode = "200", description = "Book updated", content = [Content()])]
    )
    @PutMapping("/{bookId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable("bookId") bookId: Int, @RequestBody book: BookDTO): ResponseEntity<Unit> {
        bookService.update(bookId, book)
        return ok().build()
    }

    @Operation(
        summary = "Delete book by id",
        responses = [ApiResponse(responseCode = "204", description = "Book deleted", content = [Content()])]
    )
    @DeleteMapping("/{bookId}")
    fun delete(@PathVariable("bookId") bookId: Int): ResponseEntity<Unit> {
        bookService.delete(bookId)
        return noContent().build()
    }

    @Operation(
        summary = "Retrieve book from library by user",
        responses = [ApiResponse(
            responseCode = "200", description = "Book order",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = OrderDTO::class))]
        )]
    )
    @PostMapping("/take", consumes = [APPLICATION_JSON_VALUE])
    fun takeBook(@RequestBody order: OrderDTO): ResponseEntity<OrderDTO> {
        if (isNull(order.bookId)) {
            throw ResponseStatusException(BAD_REQUEST, "Book id must be provided")
        }

        val taken = bookService.takeBook(order)
        return status(CREATED).body(taken)
    }

    @Operation(
        summary = "Return book previously ordered",
        responses = [ApiResponse(responseCode = "200", description = "Book returned", content = [Content()])]
    )
    @PostMapping("/return", consumes = [APPLICATION_JSON_VALUE])
    fun returnBook(@RequestBody order: OrderDTO): ResponseEntity<Unit> {
        if (isNull(order.id)) {
            throw ResponseStatusException(BAD_REQUEST, "Order id must be provided")
        }

        bookService.returnBook(order)
        return ok().build()
    }

    @Operation(
        summary = "Create feedback for book from reader",
        responses = [ApiResponse(responseCode = "201", description = "Feedback created", content = [Content()])]
    )
    @PostMapping("/feedback", consumes = [APPLICATION_JSON_VALUE])
    fun feedbackBook(@RequestBody feedback: FeedbackDTO): ResponseEntity<Unit> {
        if (isNull(feedback.bookId)) {
            throw ResponseStatusException(BAD_REQUEST, "Book id must be provided")
        }

        bookService.feedbackBook(feedback)

        val uri = URI.create("/library/books/${feedback.bookId}/feedbacks")
        return created(uri).build()
    }

    @Operation(
        summary = "List of feedbacks for book",
        responses = [ApiResponse(
            responseCode = "200", description = "List of feedbacks",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = FeedbackDTO::class)))]
        )]
    )
    @GetMapping("/{bookId}/feedbacks", produces = [APPLICATION_JSON_VALUE])
    fun bookFeedbacks(@PathVariable("bookId") bookId: Int): Collection<FeedbackDTO> = bookService.bookFeedbacks(bookId)

    @Operation(
        summary = "Delete feedback by id",
        responses = [ApiResponse(responseCode = "204", description = "Feedback deleted", content = [Content()])]
    )
    @DeleteMapping("/feedbacks/{feedbackId}")
    fun deleteFeedback(@PathVariable("feedbackId") feedbackId: Int): ResponseEntity<Unit> {
        bookService.deleteFeedback(feedbackId)
        return noContent().build()
    }

    @Operation(
        summary = "List of orders for book",
        responses = [ApiResponse(
            responseCode = "200", description = "List of orders",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = OrderDTO::class)))]
        )]
    )
    @GetMapping("/{bookId}/orders", produces = [APPLICATION_JSON_VALUE])
    fun bookOrders(@PathVariable("bookId") bookId: Int): Collection<OrderDTO> = bookService.bookOrders(bookId)
}
