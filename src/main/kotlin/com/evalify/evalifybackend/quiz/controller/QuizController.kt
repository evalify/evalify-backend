package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.service.QuizService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/quiz")
class QuizController(quiz_service: QuizService) {

}