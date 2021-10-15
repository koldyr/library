package com.koldyr.library.controllers

import com.koldyr.library.dto.FeedbackDTO
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Reader
import com.koldyr.library.services.ReaderService
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
import java.net.URI

/**
 * Description of class ReaderController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/library/readers")
class ReaderController(private val readerService: ReaderService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun readers(): Collection<Reader> = readerService.findAll()

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody reader: Reader): ResponseEntity<Unit> {
        val readerId: Int = readerService.create(reader)

        val uri = URI.create("/api/library/readers/$readerId")
        return created(uri).build()
    }

    @PutMapping("/{readerId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable readerId: Int, @RequestBody reader: Reader) = readerService.update(readerId, reader)

    @GetMapping("/{readerId}", produces = [APPLICATION_JSON_VALUE])
    fun readerById(@PathVariable readerId: Int): Reader = readerService.findById(readerId)

    @DeleteMapping("/{readerId}")
    fun delete(@PathVariable readerId: Int): ResponseEntity<Unit> {
        readerService.delete(readerId)

        return noContent().build()
    }

    @GetMapping("/{readerId}/orders", produces = [APPLICATION_JSON_VALUE])
    fun orders(@PathVariable readerId: Int, @RequestParam(required = false) returned: Boolean?): Collection<OrderDTO> = readerService.findOrders(readerId, returned)

    @GetMapping("/{readerId}/feedbacks", produces = [APPLICATION_JSON_VALUE])
    fun feedbacks(@PathVariable readerId: Int): Collection<FeedbackDTO> = readerService.findFeedbacks(readerId)
}
