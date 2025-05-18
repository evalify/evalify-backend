package com.evalify.evalifybackend.security.utils


import com.evalify.evalifybackend.common.logging.logger
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * This filter logs the details of incoming HTTP requests and their corresponding responses.
 * It is useful for debugging purposes, especially in development environments.
 *
 * It logs the request method, URI, headers (excluding sensitive information), and authentication details.
 * It also logs the response status after the request has been processed.
 */
class RequestDebugFilter : OncePerRequestFilter() {
    private val log by logger()
    
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestURI = request.requestURI
        val method = request.method

        log.info("Request: $method $requestURI")

        // Log request headers
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            if (headerName.lowercase() != "authorization") { // Don't log full auth token
                log.debug("Header: $headerName = ${request.getHeader(headerName)}")
            } else {
                val authHeader = request.getHeader(headerName)
                log.debug("Header: $headerName = ${authHeader.substring(0, 20.coerceAtMost(authHeader.length))}...")
            }
        }

        // Log authentication details
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            log.info("Authentication: ${auth.name}, Authorities: ${auth.authorities}")
        } else {
            log.info("No authentication found in security context")
        }

        try {
            filterChain.doFilter(request, response)
        } finally {
            log.info("Response status: ${response.status}")
        }
    }
}
