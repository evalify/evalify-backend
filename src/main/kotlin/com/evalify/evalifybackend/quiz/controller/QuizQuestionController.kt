package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.AddQuestionsToQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.AddBankQuestionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.AddQuestionsResponse
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.CreateQuizQuestionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quizQuestionAddResponse
import com.evalify.evalifybackend.quiz.service.QuizQuestionService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import com.evalify.evalifybackend.security.utils.SecurityUtils.getCurrentUserId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID


@RestController
@RequestMapping("/api/quiz")
class QuizQuestionController(private val questionService: QuizQuestionService,

) {
    /*
    * This method is used to filter questions from bank to quiz.
    * Does not allow duplicate questions to quiz from a bank.
    */
    @PostMapping("/{quizId}/question/filter")
    fun filterBankQuestionsToQuiz(
        @PathVariable quizId: UUID,
        @RequestBody addQuestionToQuizDTO: AddQuestionsToQuizDTO
//        @RequestParam(required = false) topicId : List<UUID>?,
//        @RequestParam(required = false) bankIds : List<UUID>?,
//        @RequestParam(required = false) difficulty : List<Difficulty>?,
//        @RequestParam(required = false) nofQuestions : Int,
//        @RequestParam(required = false) questionType : List<QuestionTypes>?,
//        @RequestParam(required = true) userId : String
    ): ResponseEntity<List<BankQuestionsReturnDTO>> {
        val userId = getCurrentUserId()?: throw Exception("You are not authorized to access this resource.")
        val response = questionService.addByQuestionByFilters(quizId = quizId,topicId = addQuestionToQuizDTO.topicId,difficulty = addQuestionToQuizDTO.difficulty,noOfQuestion = addQuestionToQuizDTO.noOfQuestions,
            questionTypes = addQuestionToQuizDTO.questionType,userId = userId,bankIds = addQuestionToQuizDTO.bankId)

        return ResponseEntity.ok(response)

    }
    /*
    * This method is used to add questions to the quiz directly.
     */

    @PostMapping("/{quizId}/question")
    fun addQuizQuestion(
        @PathVariable quizId: UUID,@RequestBody dto: CreateQuizQuestionDTO
    ){
        val userId: String? = SecurityUtils.getCurrentUserId()
        questionService.createQuizQuestion(dto,quizId,userId)
    }

    @PatchMapping("/{quizId}/question/{questionId}")
    fun updateQuizQuestion(
        @PathVariable quizId: UUID,
        @PathVariable questionId: UUID,
        @RequestBody quizQuestion : PatchQuestionDTO
    ){
        val userId: String? = SecurityUtils.getCurrentUserId()
        questionService.editQuizQuestion(quizId,questionId,quizQuestion,userId)
    }


    @DeleteMapping("/{quizId}/question/{questionId}")
    fun deleteQuizQuestion(
        @PathVariable questionId: UUID,
        @PathVariable quizId: UUID
    ){

        questionService.deleteQuizQuestion(quizId,questionId)
    }

    /*
    * This method is used to add manually selected questions from the bank to the quiz.
     */

    @PostMapping("/{quizId}/question/add")
    fun addBankQuestionToQuiz(
        @PathVariable quizId: UUID,
        @RequestBody dto : AddBankQuestionDTO
    ):ResponseEntity<quizQuestionAddResponse>{
        val userId = SecurityUtils.getCurrentUserId()
        val response = questionService.addSelectQuestions(quizId,dto, userId)

        return ResponseEntity.ok(response)

    }
}