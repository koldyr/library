package com.koldyr.library.controllers

import java.net.URI
import com.koldyr.library.dto.OrderDTO
import com.koldyr.library.model.Reader
import com.koldyr.library.services.ReaderService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.noContent
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Description of class ReaderController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/library/readers")
class ReaderController(private val readerService: ReaderService) {

    @GetMapping
    fun readers(): Collection<Reader> = readerService.findAll()

    @PostMapping
    fun create(@RequestBody reader: Reader): ResponseEntity<String> {
        val readerId: Int = readerService.create(reader)
        
        val uri = URI.create("/api/library/readers/$readerId")
        return created(uri).build()
    }

    @PutMapping("/{readerId}")
    fun update(@PathVariable readerId: Int, @RequestBody reader: Reader) = readerService.update(readerId, reader)

    @GetMapping("/{readerId}")
    fun readerById(@PathVariable readerId: Int): Reader = readerService.findById(readerId)
    
    @DeleteMapping("/{readerId}")
    fun delete(@PathVariable readerId: Int): ResponseEntity<Unit> {
        readerService.delete(readerId)
        
        return noContent().build()
    }

    @GetMapping("/{readerId}/orders")
    fun orders(@PathVariable readerId: Int): Collection<OrderDTO> = readerService.findOrders(readerId)
}
