package com.koldyr.library.mapper

import com.koldyr.library.model.Author
import com.koldyr.library.persistence.AuthorRepository
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.converter.BidirectionalConverter
import ma.glasnost.orika.metadata.Type
import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class AuthorConverter
 * @created: 2021-10-07
 */
class AuthorConverter(private val authorRepository: AuthorRepository) : BidirectionalConverter<Int, Author>() {
    
    override fun convertTo(authorId: Int?, type: Type<Author>?, context: MappingContext?): Author? {
        if (authorId == null) {
            return null
        }
        return authorRepository.findById(authorId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Author with id '$authorId' is not found") }
    }

    override fun convertFrom(author: Author?, type: Type<Int>?, context: MappingContext?): Int? {
        return author?.id
    }
}
