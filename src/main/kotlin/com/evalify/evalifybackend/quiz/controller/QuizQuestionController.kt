package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.AddQuestionsToQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.AddBankQuestionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.AddQuestionsResponse
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.CreateQuizQuestionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quizQuestionAddResponse
import com.evalify.evalifybackend.quiz.service.QuizQuestionService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID


@RestController
@RequestMapping("/api/quiz")
class QuizQuestionController(val questionService: QuizQuestionService,

) {


    @PostMapping("/{quizId}/section/{sectionID}/addQuestions")
    fun addBankQuestionsToQuiz(
        @PathVariable quizId: UUID,
        @PathVariable sectionID : UUID,
        @RequestBody addQuestionToQuizDTO: AddQuestionsToQuizDTO
//        @RequestParam(required = false) topicId : List<UUID>?,
//        @RequestParam(required = false) bankIds : List<UUID>?,
//        @RequestParam(required = false) difficulty : List<Difficulty>?,
//        @RequestParam(required = false) nofQuestions : Int,
//        @RequestParam(required = false) questionType : List<QuestionTypes>?,
//        @RequestParam(required = true) userId : String
    ): ResponseEntity<AddQuestionsResponse> {
        val response = questionService.addByQuestionByFilters(quizId = quizId,topicId = addQuestionToQuizDTO.topicId,difficulty = addQuestionToQuizDTO.difficulty,noOfQuestion = addQuestionToQuizDTO.noOfQuestions,
            questionTypes = addQuestionToQuizDTO.questionType,userId = addQuestionToQuizDTO.userId,bankIds = addQuestionToQuizDTO.bankId,sectionId = sectionID)

        return ResponseEntity.ok(response)

    }


    @PostMapping("/{quizId}/addQuestion")
    fun addQuizQuestion(
        @PathVariable quizId: UUID,@RequestBody dto: CreateQuizQuestionDTO
    ){
        val userId: String? = SecurityUtils.getCurrentUserId()
        questionService.createQuizQuestion(dto,quizId,userId)
    }

    @PostMapping("/{quizId}/addSelectQuestion/")
    fun addBankQuestionToQuiz(
        @PathVariable quizId: UUID,
        @RequestParam dto : AddBankQuestionDTO
    ):ResponseEntity<quizQuestionAddResponse>{
        val userId = SecurityUtils.getCurrentUserId()
        val response = questionService.addSelectQuestions(quizId,dto, userId)

        return ResponseEntity.ok(response)

    }
}