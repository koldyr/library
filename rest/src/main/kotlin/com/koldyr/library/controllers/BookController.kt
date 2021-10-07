package com.koldyr.library.controllers

import com.koldyr.library.dto.BookDTO
import com.koldyr.library.services.BookService
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
import java.net.URI

/**
 * Description of class BookController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/library/books")
class BookController(private val bookService: BookService) {

    @GetMapping
    fun books(@RequestParam(required = false) available: Boolean): Collection<BookDTO> = bookService.findAll(available)

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
}
