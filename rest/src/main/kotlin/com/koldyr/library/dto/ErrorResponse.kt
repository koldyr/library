package com.koldyr.library.dto

import java.time.LocalDateTime

/**
 * Description of class ErrorResponse
 * @created: 2022-02-12
 */
data class ErrorResponse(
    var status: Short? = null,
    var timestamp: LocalDateTime? = null,
    var error: String? = null
) {
    var path: String? = null
    var exception: String? = null
    var trace: String? = null
}