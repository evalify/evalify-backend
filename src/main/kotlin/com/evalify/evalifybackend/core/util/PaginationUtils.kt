package com.evalify.evalifybackend.core.util

import com.evalify.evalifybackend.core.DTO.PageResponse
import org.springframework.data.domain.Page

/** Utility for pagination operations */
object PaginationUtils {

    /** Converts Spring Data Page to custom PageResponse */
    fun <T> toPageResponse(page: Page<T>): PageResponse<T> {
        return PageResponse(
                content = page.content,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                first = page.isFirst,
                last = page.isLast,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious()
        )
    }

    /** Converts Spring Data Page with transformed content */
    fun <T, R> toPageResponse(page: Page<T>, transform: (List<T>) -> List<R>): PageResponse<R> {
        return PageResponse(
                content = transform(page.content),
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                first = page.isFirst,
                last = page.isLast,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious()
        )
    }
}
