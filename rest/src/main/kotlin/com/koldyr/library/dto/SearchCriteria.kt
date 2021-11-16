package com.koldyr.library.dto

/**
 * Description of class SearchCriteria
 * @created: 2021-10-07
 */
data class SearchCriteria(
        var title: String? = null,
        var author: String? = null,
        var genre: Array<String>? = null,
        var publisher: String? = null,
        var publishYearFrom: Int? = null,
        var publishYearTill: Int? = null,
        var note: String? = null,
        var page: PageDTO? = null,
        var sort: SortDTO? = null
)

data class PageDTO (
    var size: Int = 100,
    var index: Int = 0
)

data class SortDTO (
    var name: String? = null,
    var order: String = "ASC"
)

class PageResultDTO<T>(val result: List<T>) {
    var page: Int? = null
    var size: Int? = null
    var total: Long? = null
}
