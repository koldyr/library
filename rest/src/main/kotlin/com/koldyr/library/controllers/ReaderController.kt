package com.koldyr.library.controllers

import com.koldyr.library.model.Reader
import com.koldyr.library.services.ReaderService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
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
 * Description of class ReaderController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/library/readers")
class ReaderController(private val readerService: ReaderService) {

    @GetMapping
    fun persons(): Collection<Reader> = readerService.findAll()

    @PostMapping
    fun create(@RequestBody reader: Reader): ResponseEntity<String> {
        val readerId: Int = readerService.create(reader)
        
        val uri = URI.create("/api/library/readers/$readerId")
        return created(uri).build()
    }

    @PutMapping("/{personId}")
    fun update(@PathVariable personId: Int, @RequestBody person: Reader) = readerService.update(personId, person)

    @GetMapping("/{personId}")
    fun personById(@PathVariable personId: Int): Reader = readerService.findById(personId)
    
    @DeleteMapping("/{personId}")
    fun delete(@PathVariable personId: Int): ResponseEntity<Unit> {
        readerService.delete(personId)
        
        return noContent().build()
    }
}
