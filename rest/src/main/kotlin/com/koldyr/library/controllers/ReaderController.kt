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
class ReaderController(private val readerService: ReaderService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun readers(): Collection<ReaderDTO> = readerService.findAll()

    @PutMapping("/{readerId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable readerId: Int, @RequestBody reader: ReaderDTO): ResponseEntity<Unit> {
        readerService.update(readerId, reader)
        return ok().build()
    }

    @GetMapping("/{readerId}", produces = [APPLICATION_JSON_VALUE])
    fun readerById(@PathVariable readerId: Int): ReaderDTO = readerService.findById(readerId)

    @GetMapping("/me", produces = [APPLICATION_JSON_VALUE])
    fun currentReader(): ReaderDTO = readerService.currentReader()

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
