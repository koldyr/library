package com.koldyr.library.dto

/**
 * Description of class SearchCriteria
 * @created: 2021-10-07
 */
class SearchCriteria {
    var title: String? = null
    var author: String? = null
    var genre: String? = null
    var publisher: String? = null
    var publishYearFrom: Int? = null
    var publishYearTill: Int? = null
    var note: String? = null

    var page: PageDTO? = null
    var sort: SortDTO? = null
}
