package com.koldyr.library.controllers

import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.model.Author
import com.koldyr.library.services.AuthorService
import com.koldyr.library.services.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * Description of class AuthorController
 * @created: 2021-10-06
 */
@RestController
@RequestMapping("/api/library/authors")
class AuthorController(
        private val authorService: AuthorService,
        private val bookService: BookService) {

    @GetMapping
    fun authors(): List<AuthorDTO> = authorService.findAll()

    @PostMapping
    fun create(@RequestBody author: AuthorDTO): ResponseEntity<String> {
        val authorId: Int = authorService.create(author)

        val uri = URI.create("/api/library/authors/$authorId")
        return ResponseEntity.created(uri).build()
    }

    @PutMapping("/{authorId}")
    fun update(@PathVariable authorId: Int, @RequestBody author: AuthorDTO) = authorService.update(authorId, author)

    @GetMapping("/{authorId}")
    fun authorById(@PathVariable authorId: Int): Author = authorService.findById(authorId)

    @DeleteMapping("/{authorId}")
    fun delete(@PathVariable authorId: Int): ResponseEntity<Unit> {
        authorService.delete(authorId)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{authorId}/books")
    fun books(@PathVariable authorId: Int): Collection<BookDTO> = bookService.findBooks(authorId)
}
