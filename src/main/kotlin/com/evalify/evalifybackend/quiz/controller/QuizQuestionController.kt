package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.AddQuestionsResponse
import com.evalify.evalifybackend.quiz.service.QuizQuestionService
import org.aspectj.weaver.patterns.TypePatternQuestions
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID


@RestController
class QuizQuestionController(val questionService: QuizQuestionService) {


    @PostMapping("/quiz/{quizId}/section/{sectionID}/addQuestions")
    fun addBankQuestionsToQuiz(
        @PathVariable quizId: UUID,
        @PathVariable sectionID : UUID,
        @RequestParam(required = false) topicId : List<UUID>,
        @RequestParam(required = false) bankIds : List<UUID>,
        @RequestParam(required = false) difficulty : List<Difficulty>,
        @RequestParam(required = false) nofQuestions : Int,
        @RequestParam(required = false) questionType : List<QuestionTypes>,
        @RequestParam(required = true) userId : UUID
    ): ResponseEntity<AddQuestionsResponse> {
        val response = questionService.addByQuestionByFilters(quizId = quizId,topicId = topicId,difficulty = difficulty,noOfQuestion = nofQuestions,
            questionTypes = questionType,userId = userId,bankIds = bankIds,sectionId = sectionID)

        return ResponseEntity.ok(response)

    }
}