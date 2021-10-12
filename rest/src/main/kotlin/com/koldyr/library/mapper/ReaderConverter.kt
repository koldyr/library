package com.koldyr.library.mapper

import com.koldyr.library.model.Reader
import com.koldyr.library.persistence.ReaderRepository
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.converter.BidirectionalConverter
import ma.glasnost.orika.metadata.Type
import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class ReaderConverter
 * @created: 2021-10-07
 */
class ReaderConverter(private val readerRepository: ReaderRepository) : BidirectionalConverter<Int, Reader>() {

    override fun convertTo(readerId: Int?, type: Type<Reader>?, context: MappingContext?): Reader? {
        if (readerId == null) {
            return null
        }
        return readerRepository.findById(readerId)
                .orElseThrow { ResponseStatusException(BAD_REQUEST, "Reader with id '$readerId' is not found") }
    }

    override fun convertFrom(reader: Reader?, type: Type<Int>?, context: MappingContext?): Int? {
        return reader?.id
    }
}
