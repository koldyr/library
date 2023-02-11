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
import com.koldyr.library.dto.BookDTO
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
class BookController(private val bookService: BookService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun books(@RequestParam(required = false) available: Boolean): Collection<BookDTO> = bookService.findAll(available)

    @PostMapping("/search", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun searchBooks(@RequestBody(required = false) criteria: SearchCriteria?): PageResultDTO<BookDTO> = bookService.findBooks(criteria)

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody book: BookDTO): ResponseEntity<Unit> {
        val bookId = bookService.create(book)

        val uri = URI.create("/library/books/${bookId}")
        return created(uri).build()
    }

    @GetMapping("/{bookId}", produces = [APPLICATION_JSON_VALUE])
    fun bookById(@PathVariable bookId: Int): BookDTO = bookService.findById(bookId)

    @PutMapping("/{bookId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable bookId: Int, @RequestBody book: BookDTO): ResponseEntity<Unit> {
        bookService.update(bookId, book)
        return ok().build()
    }

    @DeleteMapping("/{bookId}")
    fun delete(@PathVariable bookId: Int): ResponseEntity<Unit> {
        bookService.delete(bookId)
        return noContent().build()
    }

    @PostMapping("/take", consumes = [APPLICATION_JSON_VALUE])
    fun takeBook(@RequestBody order: OrderDTO): ResponseEntity<OrderDTO> {
        if (isNull(order.bookId)) {
            throw ResponseStatusException(BAD_REQUEST, "Book id must be provided")
        }

        val taken = bookService.takeBook(order)
        return status(CREATED).body(taken)
    }

    @PostMapping("/return", consumes = [APPLICATION_JSON_VALUE])
    fun returnBook(@RequestBody order: OrderDTO): ResponseEntity<Unit> {
        if (isNull(order.id)) {
            throw ResponseStatusException(BAD_REQUEST, "Order id must be provided")
        }

        bookService.returnBook(order)
        return ok().build()
    }

    @PostMapping("/feedback", consumes = [APPLICATION_JSON_VALUE])
    fun feedbackBook(@RequestBody feedback: FeedbackDTO): ResponseEntity<Unit> {
        if (isNull(feedback.bookId)) {
            throw ResponseStatusException(BAD_REQUEST, "Book id must be provided")
        }

        bookService.feedbackBook(feedback)

        val uri = URI.create("/library/books/${feedback.bookId}/feedbacks")
        return created(uri).build()
    }

    @GetMapping("/{bookId}/feedbacks", produces = [APPLICATION_JSON_VALUE])
    fun bookFeedbacks(@PathVariable bookId: Int): Collection<FeedbackDTO> = bookService.bookFeedbacks(bookId)

    @DeleteMapping("/feedbacks/{feedbackId}")
    fun deleteFeedback(@PathVariable feedbackId: Int): ResponseEntity<Unit> {
        bookService.deleteFeedback(feedbackId)
        return noContent().build()
    }

    @GetMapping("/{bookId}/orders", produces = [APPLICATION_JSON_VALUE])
    fun bookOrders(@PathVariable bookId: Int): Collection<OrderDTO> = bookService.bookOrders(bookId)
}
