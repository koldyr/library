package com.koldyr.library.services

import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import com.koldyr.library.dto.AuthorDTO
import com.koldyr.library.model.Author
import com.koldyr.library.persistence.AuthorRepository


/**
 * Description of class AuthorServiceImpl
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-08
 */
@Service
@Transactional
class AuthorServiceImpl(
    private val authorRepository: AuthorRepository,
) : AuthorService, BaseLibraryService() {

    @PreAuthorize("hasAuthority('read_author')")
    override fun findAll(): List<AuthorDTO> = authorRepository.findAll().map { mapper.map(it, AuthorDTO::class.java) }

    @PreAuthorize("hasAuthority('read_author')")
    override fun search(search: String): List<AuthorDTO> {
        val filter = createFilter(search)
        return authorRepository.findAll(filter).map { mapper.map(it, AuthorDTO::class.java) }
    }

    @PreAuthorize("hasAuthority('modify_author')")
    override fun create(author: AuthorDTO): Int {
        author.id = null
        val newAuthor = mapper.map(author, Author::class.java)
        val saved = authorRepository.save(newAuthor)
        return saved.id!!
    }

    @PreAuthorize("hasAuthority('read_author')")
    override fun findById(authorId: Int): AuthorDTO {
        val author = find(authorId)
        return mapper.map(author, AuthorDTO::class.java)
    }

    @PreAuthorize("hasAuthority('modify_author')")
    override fun update(authorId: Int, author: AuthorDTO) {
        val persisted = find(authorId)

        author.id = authorId
        mapper.map(author, persisted)

        authorRepository.save(persisted)
    }

    @PreAuthorize("hasAuthority('modify_author')")
    override fun delete(authorId: Int) = authorRepository.deleteById(authorId)

    private fun find(authorId: Int): Author = authorRepository
        .findById(authorId)
        .orElseThrow { ResponseStatusException(NOT_FOUND, "Author with id '$authorId' is not found") }

    private fun createFilter(search: String): Specification<Author> {
        return Specification<Author> { author, _, builder ->
            val value = search.lowercase()
            builder.or(
                    builder.like(builder.lower(author.get("firstName")), "%$value%"),
                    builder.like(builder.lower(author.get("lastName")), "%$value%")
            )
        }
    }
}
