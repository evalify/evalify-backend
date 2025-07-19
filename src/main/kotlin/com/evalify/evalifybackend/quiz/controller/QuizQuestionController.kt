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

/**
 * Controller managing quiz question operations and configurations.
 *
 * Use Cases:
 * - Adding questions to quizzes from question banks
 * - Filtering and selecting questions based on criteria
 * - Direct question creation within quizzes
 * - Updating and managing quiz questions
 * - Question organization and management within quizzes
 *
 * This controller handles all question-related operations within quizzes,
 * including both bank-sourced and directly created questions.
 */
@RestController
@RequestMapping("/api/quiz")
class QuizQuestionController(private val questionService: QuizQuestionService,

) {
    /*
    * This method is used to filter questions from bank to quiz.
    * Does not allow duplicate questions to quiz from a bank.
    */
    /**
     * Filters and adds questions from question banks to a quiz based on specified criteria.
     *
     * Use Cases:
     * - Smart question selection for quizzes
     * - Topic-based question filtering
     * - Difficulty-based question filtering
     * - Question type filtering
     * - Preventing duplicate questions
     *
     * @param quizId The UUID of the quiz to add questions to
     * @param addQuestionToQuizDTO Contains filter criteria like topics, difficulty, and question types
     * @return ResponseEntity containing the list of filtered and added questions
     * @throws Exception if user is not authorized
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

    /**
     * Adds a new question directly to a quiz.
     *
     * Use Cases:
     * - Creating custom questions for a quiz
     * - Adding unique questions not in question bank
     * - Supporting quiz-specific questions
     * - Quick question creation
     *
     * @param quizId The UUID of the quiz to add the question to
     * @param dto Contains the question details to be created
     */
    @PostMapping("/{quizId}/question")
    fun addQuizQuestion(
        @PathVariable quizId: UUID,@RequestBody dto: CreateQuizQuestionDTO
    ){
        val userId: String? = SecurityUtils.getCurrentUserId()
        questionService.createQuizQuestion(dto,quizId,userId)
    }

    /**
     * Updates an existing question within a quiz.
     *
     * Use Cases:
     * - Modifying question content
     * - Updating question parameters
     * - Fixing errors in questions
     * - Adjusting question difficulty
     *
     * @param quizId The UUID of the quiz containing the question
     * @param questionId The UUID of the question to update
     * @param quizQuestion Contains the updated question details
     */
    @PatchMapping("/{quizId}/question/{questionId}")
    fun updateQuizQuestion(
        @PathVariable quizId: UUID,
        @PathVariable questionId: UUID,
        @RequestBody quizQuestion : PatchQuestionDTO
    ){
        val userId: String? = SecurityUtils.getCurrentUserId()
        questionService.editQuizQuestion(quizId,questionId,quizQuestion,userId)
    }


    /**
     * Removes a question from a quiz.
     *
     * Use Cases:
     * - Removing inappropriate questions
     * - Adjusting quiz length
     * - Managing quiz content
     * - Question quality control
     *
     * @param questionId The UUID of the question to delete
     * @param quizId The UUID of the quiz containing the question
     */
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

    /**
     * Adds selected questions from a question bank to a quiz.
     *
     * Use Cases:
     * - Manual question selection
     * - Question bank integration
     * - Quiz content curation
     * - Reusing existing questions
     *
     * @param quizId The UUID of the quiz to add questions to
     * @param dto Contains the selected bank questions to add
     * @return ResponseEntity with confirmation of added questions
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