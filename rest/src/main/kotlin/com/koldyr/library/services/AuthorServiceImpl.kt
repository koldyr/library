package com.koldyr.library.services

import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.model.Author
import com.koldyr.library.persistence.AuthorRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.stream.Collectors.*

open class AuthorServiceImpl(
    private val authorRepository: AuthorRepository,
    private val mapper: MapperFacade
) : AuthorService {

    override fun findAll(): List<AuthorDTO> = authorRepository.findAll().stream()
        .map { mapper.map(it, AuthorDTO::class.java) }
        .collect(toList())

    @Transactional
    override fun create(author: AuthorDTO): Int {
        author.id = null
        val newAuthor = mapper.map(author, Author::class.java)
        val saved = authorRepository.save(newAuthor)
        return saved.id!!
    }

    override fun findById(authorId: Int): AuthorDTO {
        val author = find(authorId)
        return mapper.map(author, AuthorDTO::class.java)
    }

    @Transactional
    override fun update(authorId: Int, author: AuthorDTO) {
        val persisted = find(authorId)

        author.id = authorId
        mapper.map(author, persisted)

        authorRepository.save(persisted)
    }

    @Transactional
    override fun delete(authorId: Int) {
        authorRepository.deleteById(authorId)
    }
    
    private fun find(authorId: Int): Author {
        return authorRepository.findById(authorId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Author with id '$authorId' is not found") }
    }
}
