package com.koldyr.library.mapper

import com.koldyr.library.model.Genre
import com.koldyr.library.persistence.GenreRepository
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.converter.BidirectionalConverter
import ma.glasnost.orika.metadata.Type
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.server.ResponseStatusException
import java.util.Objects.isNull

/**
 * Description of class GenreConverter
 * @created: 2022-02-14
 */
class GenreConverter(private val genreRepository: GenreRepository) : BidirectionalConverter<String, Genre>() {

    override fun convertTo(genre: String?, type: Type<Genre>?, context: MappingContext?): Genre? {
        if (isNull(genre)) {
            return null
        }
        return genreRepository.findByName(genre!!.uppercase())
                .orElseThrow { ResponseStatusException(BAD_REQUEST, "Genre with id '$genre' is not found") }
    }

    override fun convertFrom(genre: Genre?, type: Type<String>?, context: MappingContext?): String? {
        return genre?.name
    }
}
