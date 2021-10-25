package com.koldyr.library.dto

import java.time.LocalDateTime

/**
 * Description of class OrderDTO
 * @created: 2021-10-06
 */
data class OrderDTO(
        var id: Int? = null,
        var bookId: Int? = null,
        var readerId: Int? = null,
        var ordered: LocalDateTime? = null,
        var returned: LocalDateTime? = null,
        var notes: String? = null
)
