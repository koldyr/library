package com.koldyr.library.dto

import java.time.LocalDateTime
import jakarta.validation.constraints.Size

/**
 * Description of class OrderDTO
 *
 * @author: d.halitski@gmail.com
 * @created: 2021-10-06
 */
data class OrderDTO(
    var id: Int? = null,
    var bookId: Int? = null,
    var readerId: Int? = null,
    var ordered: LocalDateTime? = null,
    var returned: LocalDateTime? = null,
    @field:Size(max = 255)
    var notes: String? = null,
    var book: BookDTO? = null
)
