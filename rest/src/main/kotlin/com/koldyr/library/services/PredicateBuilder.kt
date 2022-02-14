package com.koldyr.library.services

import com.koldyr.library.dto.SearchCriteria
import com.koldyr.library.model.Book
import com.koldyr.library.model.Genre
import com.koldyr.library.model.GenreNames
import org.apache.commons.lang3.ObjectUtils.isNotEmpty
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.Objects.nonNull
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate
import kotlin.reflect.full.declaredMemberProperties

/**
 * Description of class PredicateBuilder
 * @created: 2022-02-10
 */
@Component
class PredicateBuilder {

    fun booksFilter(criteria: SearchCriteria): Specification<Book>? {
        if (!hasCriteria(criteria)) {
            return null
        }

        return Specification<Book> { book, _, builder ->
            val filters: MutableList<Predicate> = mutableListOf()
            
            if (nonNull(criteria.title)) {
                val predicate = builder.like(builder.lower(book.get("title")), "%${criteria.title?.lowercase()}%")
                filters.add(predicate)
            }

            if (isNotEmpty(criteria.genres)) {
                val values: List<GenreNames> = criteria.genres!!.filter { nonNull(it) }.map { GenreNames.valueOf(it.uppercase()) }
                if (values.isNotEmpty()) {
                    val genres = book.join<Book, Genre>("genres")
                    val name = genres.get<String>("name")
                    val predicate = name.`in`(values)
                    filters.add(predicate)
                }
            }

            if (nonNull(criteria.publisher)) {
                val publishingHouse = book.get<String>("publishingHouse")
                val predicate = builder.like(builder.lower(publishingHouse), "%${criteria.publisher?.lowercase()}%")
                filters.add(predicate)
            }

            if (nonNull(criteria.note)) {
                val note = book.get<String>("note")
                val predicate = builder.like(builder.lower(note), "%${criteria.note?.lowercase()}%")
                filters.add(predicate)
            }

            if (nonNull(criteria.publishYearFrom) || nonNull(criteria.publishYearTill)) {
                val yearFrom: Int = if (criteria.publishYearFrom == null) 1000 else criteria.publishYearFrom!!
                val yearTo: Int = if (criteria.publishYearTill == null) 9999 else (criteria.publishYearTill!! + 1)
                val publicationDate: Path<LocalDate> = book.get("publicationDate")
                val predicate = builder.between(publicationDate, LocalDate.of(yearFrom, 1, 1), LocalDate.of(yearTo, 1, 1))
                filters.add(predicate)
            }

            builder.and(*filters.toTypedArray())

        }
    }

    private fun hasCriteria(criteria: SearchCriteria?): Boolean {
        if (criteria == null) {
            return false
        }
        return SearchCriteria::class.declaredMemberProperties
            .filter { it.name != "page" && it.name != "sort" }
            .any { nonNull(it.get(criteria)) }
    }

}
