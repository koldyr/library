package com.koldyr.library.controllers

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.dto.SearchCriteria
import com.koldyr.library.services.BookService
import org.springframework.http.HttpStatus
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
import java.net.URI
import java.util.*

/**
 * Description of class BookController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/library/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun books(@RequestParam(required = false) available: Boolean): Collection<BookDTO> = bookService.findAll(available)

    @PostMapping("/search")
    fun searchBooks(@RequestBody criteria: SearchCriteria): Collection<BookDTO> = bookService.findBooks(criteria)

    @PostMapping
    fun create(@RequestBody book: BookDTO): ResponseEntity<Unit> {
        val bookId = bookService.create(book)

        val uri = URI.create("/api/library/books/${bookId}")
        return created(uri).build()
    }

    @GetMapping("/{bookId}")
    fun bookById(@PathVariable bookId: Int): BookDTO = bookService.findById(bookId)

    @PutMapping("/{bookId}")
    fun update(@PathVariable bookId: Int, @RequestBody book: BookDTO) = bookService.update(bookId, book)

    @DeleteMapping("/{bookId}")
    fun delete(@PathVariable bookId: Int): ResponseEntity<Unit> {
        bookService.delete(bookId)

        return noContent().build()
    }

    @PostMapping("/{bookId}/take")
    fun takeBook(@PathVariable bookId: Int, @RequestBody order: OrderDTO): OrderDTO {
        if (Objects.isNull(order.readerId)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Reader id must be provided")
        }

        order.bookId = bookId
        return bookService.takeBook(order)
    }

    @PostMapping("/{bookId}/return")
    fun returnBook(@PathVariable bookId: Int, @RequestBody order: OrderDTO) {
        if (Objects.isNull(order.id)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Order id must be provided")
        }

        order.bookId = bookId
        bookService.returnBook(order)
    }

    @PostMapping("/{bookId}/feedback")
    fun feedbackBook(@PathVariable bookId: Int, @RequestBody feedback: FeedbackDTO): ResponseEntity<Unit> {
        if (Objects.isNull(feedback.readerId)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Reader id must be provided")
        }

        bookService.feedbackBook(bookId, feedback)

        val uri = URI.create("/api/library/books/${bookId}/feedbacks")
        return created(uri).build()
    }

    @GetMapping("/{bookId}/feedbacks")
    fun bookFeedbacks(@PathVariable bookId: Int): Collection<FeedbackDTO> = bookService.bookFeedbacks(bookId)
}
