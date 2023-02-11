package com.koldyr.library.services

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.DataAccessException
import org.springframework.web.server.ServerErrorException
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

/**
 * Description of class InternalExceptionResolver
 *
 * @author d.halitski@gmail.com
 * @created: 2023-02-09
 */
class InternalExceptionResolver : HandlerExceptionResolver {

    override fun resolveException(request: HttpServletRequest, response: HttpServletResponse, handler: Any?, ex: Exception): ModelAndView? {
        when (ex) {
            is ServerErrorException -> {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.message)
            }
            is DataAccessException -> {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.javaClass.simpleName)
            }
        }
        return null
    }
}
