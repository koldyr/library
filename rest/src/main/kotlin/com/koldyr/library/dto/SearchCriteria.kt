package com.koldyr.library.dto

/**
 * Description of class SearchCriteria
 * @created: 2021-10-07
 */
class SearchCriteria {
    var title: String? = null
    var author: String? = null
    var genre: Array<String>? = null
    var publisher: String? = null
    var publishYear: Int? = null
    var publishYearFrom: Int? = null
    var publishYearTill: Int? = null
    var note: String? = null
}
