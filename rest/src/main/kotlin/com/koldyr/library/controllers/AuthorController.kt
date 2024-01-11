package com.koldyr.library.controllers

import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
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
@RequestMapping("/api/v1/authors")
@Tag(name = "AuthorController", description = "Author operations")
class AuthorController(
    private val authorService: AuthorService, private val bookService: BookService
) : BaseController() {

    @Operation(
        summary = "Search for authors",
        parameters = [Parameter(
            name = "search", description = "Look-up for authors with first/last name containing passed text",
            `in` = ParameterIn.QUERY, schema = Schema(implementation = String::class), allowEmptyValue = true
        )],
        responses = [ApiResponse(
            responseCode = "200", description = "List of found Authors",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = AuthorDTO::class)))]
        )]
    )
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun authors(@RequestParam("search") search: String?): List<AuthorDTO> {
        if (search.isNullOrEmpty()) {
            return authorService.findAll()
        }
        return authorService.search(search)
    }

    @Operation(
        summary = "Create new author",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Author attributes", required = true,
            content = [
                Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = AuthorDTO::class, requiredMode = Schema.RequiredMode.REQUIRED))
            ]
        ),
        responses = [ApiResponse(responseCode = "201", description = "Author created", content = [Content()])]
    )
    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody @Valid author: AuthorDTO): ResponseEntity<Unit> {
        val authorId: Int = authorService.create(author)

        val uri = URI.create("/api/v1/authors/$authorId")
        return created(uri).build()
    }

    @Operation(
        summary = "Update author's properties",
        parameters = [
            Parameter(name = "authorId", description = "Author identificator")
        ],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Author attributes",
            required = true,
            content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = AuthorDTO::class, requiredMode = Schema.RequiredMode.REQUIRED))]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Author updated", content = [Content()]),
        ]
    )
    @PutMapping("/{authorId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable("authorId") authorId: Int, @RequestBody @Valid author: AuthorDTO): ResponseEntity<Unit> {
        authorService.update(authorId, author)
        return ok().build()
    }

    @Operation(
        summary = "Get author by id",
        parameters = [Parameter(name = "authorId", description = "Author identificator")],
        responses = [
            ApiResponse(responseCode = "200", description = "Author data", content = [Content(schema = Schema(implementation = AuthorDTO::class))]),
        ]
    )
    @GetMapping("/{authorId}", produces = [APPLICATION_JSON_VALUE])
    fun authorById(@PathVariable("authorId") authorId: Int): AuthorDTO = authorService.findById(authorId)

    @Operation(
        summary = "Delete author",
        parameters = [Parameter(name = "authorId", description = "Author identificator")],
        responses = [
            ApiResponse(responseCode = "204", description = "Author deleted", content = [Content()]),
        ]
    )
    @DeleteMapping("/{authorId}")
    fun delete(@PathVariable("authorId") authorId: Int): ResponseEntity<Unit> {
        authorService.delete(authorId)

        return noContent().build()
    }

    @Operation(
        summary = "Get list of books for author",
        parameters = [Parameter(name = "authorId", description = "Author identificator")],
        responses = [ApiResponse(
            responseCode = "200",
            description = "List of found Authors",
            content = [Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = BookDTO::class)))]
        )]
    )
    @GetMapping("/{authorId}/books", produces = [APPLICATION_JSON_VALUE])
    fun books(@PathVariable("authorId") authorId: Int): Collection<BookDTO> = bookService.findBooks(authorId)
}
