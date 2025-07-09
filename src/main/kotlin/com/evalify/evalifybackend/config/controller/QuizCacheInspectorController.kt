package com.evalify.evalifybackend.config.controller


import com.evalify.evalifybackend.config.service.QuizCacheInspectorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/cache/inspect")
class QuizCacheInspectorController(
    private val quizCacheInspectorService: QuizCacheInspectorService
) {

    @GetMapping("/questions")
    fun getAllStudentQuestionsCache(): ResponseEntity<Any> {
        val result = quizCacheInspectorService.getAllStudentQuestionsCache()
        return ResponseEntity.ok(result)
    }

    @GetMapping("/answers")
    fun getAllStudentAnswersCache(): ResponseEntity<Any> {
        val result = quizCacheInspectorService.getAllStudentAnswersCache()
        return ResponseEntity.ok(result)
    }
}