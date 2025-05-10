package com.evalify.evalifybackend.security.utils

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class RequestDebugFilter : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(RequestDebugFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestURI = request.requestURI
        val method = request.method
        
        logger.info("Request: $method $requestURI")
        
        // Log request headers
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            if (headerName.lowercase() != "authorization") { // Don't log full auth token
                logger.debug("Header: $headerName = ${request.getHeader(headerName)}")
            } else {
                val authHeader = request.getHeader(headerName)
                logger.debug("Header: $headerName = ${authHeader.substring(0, Math.min(20, authHeader.length))}...")
            }
        }
        
        // Log authentication details
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            logger.info("Authentication: ${auth.name}, Authorities: ${auth.authorities}")
        } else {
            logger.info("No authentication found in security context")
        }
        
        try {
            filterChain.doFilter(request, response)
        } finally {
            logger.info("Response status: ${response.status}")
        }
    }
}
