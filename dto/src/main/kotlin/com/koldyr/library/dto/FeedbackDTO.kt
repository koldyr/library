package com.koldyr.library.dto

import java.time.LocalDateTime

/**
 * Description of class FeedbackDTO
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-07
 */
data class FeedbackDTO(
    var id: Int? = null,
    var readerId: Int? = null,
    var bookId: Int? = null,
    var date: LocalDateTime? = null,
    var text: String? = null,
    var rate: Int = 0
)
