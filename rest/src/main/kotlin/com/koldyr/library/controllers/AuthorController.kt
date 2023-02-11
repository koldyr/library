package com.koldyr.library.controllers

import java.net.URI
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.dto.BookDTO
import com.koldyr.library.services.AuthorService
import com.koldyr.library.services.BookService

/**
 * Description of class AuthorController
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-06
 */
@RestController
@RequestMapping("/library/authors")
class AuthorController(
    private val authorService: AuthorService,
    private val bookService: BookService
) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun authors(@RequestParam search: String?): List<AuthorDTO> {
        if (search.isNullOrEmpty()) {
            return authorService.findAll()
        }
        return authorService.search(search)
    }

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody author: AuthorDTO): ResponseEntity<Unit> {
        val authorId: Int = authorService.create(author)

        val uri = URI.create("/library/authors/$authorId")
        return created(uri).build()
    }

    @PutMapping("/{authorId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable authorId: Int, @RequestBody author: AuthorDTO): ResponseEntity<Unit> {
        authorService.update(authorId, author)
        return ok().build()
    }

    @GetMapping("/{authorId}", produces = [APPLICATION_JSON_VALUE])
    fun authorById(@PathVariable authorId: Int): AuthorDTO = authorService.findById(authorId)

    @DeleteMapping("/{authorId}")
    fun delete(@PathVariable authorId: Int): ResponseEntity<Unit> {
        authorService.delete(authorId)

        return noContent().build()
    }

    @GetMapping("/{authorId}/books", produces = [APPLICATION_JSON_VALUE])
    fun books(@PathVariable authorId: Int): Collection<BookDTO> = bookService.findBooks(authorId)
}
