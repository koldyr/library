package com.koldyr.library.services

import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.model.Author
import com.koldyr.library.persistence.AuthorRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

open class AuthorServiceImpl(
        private val authorRepository: AuthorRepository,
        private val mapper: MapperFacade) : AuthorService {

    override fun findAll(): List<AuthorDTO> = authorRepository.findAll().map { mapper.map(it, AuthorDTO::class.java) }

    override fun search(search: String): List<AuthorDTO> {
        val filter: Specification<Author> = createFilter(search)
        return authorRepository.findAll(filter).map { mapper.map(it, AuthorDTO::class.java) }
    }

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

    private fun createFilter(search: String): Specification<Author> {
        val value = search.lowercase()
        return Specification<Author> { author, _, builder ->
            builder.or(
                builder.like(builder.lower(author.get("firstName")), "%$value%"),
                builder.like(builder.lower(author.get("lastName")), "%$value%")
            )
        }
    }
}
