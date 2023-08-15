package com.koldyr.library.dto

import jakarta.validation.constraints.Size

/**
 * Description of class SearchCriteria
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-07
 */
data class SearchCriteria(
    @field:Size(max = 255)
    var title: String? = null,
    @field:Size(max = 255)
    var author: String? = null,
    var genres: Set<String>? = null,
    @field:Size(max = 255)
    var publisher: String? = null,
    var publishYearFrom: Int? = null,
    var publishYearTill: Int? = null,
    @field:Size(max = 255)
    var note: String? = null,
    var page: PageDTO? = null,
    var sort: SortDTO? = null
)

data class PageDTO(
    var size: Int = 100,
    var index: Int = 0
)

data class SortDTO(
    var name: String? = null,
    var order: String = "ASC"
)

class PageResultDTO<T>(val result: List<T>) {
    var page: Int? = null
    var size: Int? = null
    var total: Long? = null
}
