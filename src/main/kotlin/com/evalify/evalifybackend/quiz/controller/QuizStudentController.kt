package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.StartQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.CodingResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.DescriptiveResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.FileUploadResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.FillUpResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.MCQResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.MMCQResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.MatchResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.TrueFalseResponseDTO
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.quiz.question.repository.QuizQuestionRepository
import com.evalify.evalifybackend.quiz.service.QuizCacheService
import com.evalify.evalifybackend.quiz.service.QuizStudentService
import com.evalify.evalifybackend.security.utils.SecurityUtils.getCurrentUserId
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api/quiz/{quizId}")
@Transactional
class QuizStudentController(
    private val quizStudentService: QuizStudentService,
    private val quizCacheService: QuizCacheService,
    private val quizQuestionRepository: QuizQuestionRepository,
    private val questionRepository: QuestionRepository,
    private val objectMapper : ObjectMapper
) {
    @PostMapping("/start")
    fun startQuiz(@PathVariable quizId: UUID, request: HttpServletRequest, @RequestBody dto: StartQuizDTO)
    : ResponseEntity<QuizQuestionReturnDTO?> {
        val requestTime = Instant.now()
        val studentId = getCurrentUserId()

        val result = quizStudentService.startQuiz(
            quizId = quizId,
            studentId = studentId,
            ipAddress = request.remoteAddr,
            requestTime = requestTime,
            password = dto.password,
            quizCacheService = quizCacheService
        )

        return ResponseEntity.ok(result)
    }

    @PatchMapping("/update")
    fun updateQuiz(@PathVariable quizId: UUID, @RequestBody responses: List<Map<String,Any>>? = null){
        val studentId = getCurrentUserId()


        if(responses == null)
        {
            val responsesFromCache = quizCacheService.getAllAnswers(quizId = quizId,studentId = studentId)
            quizStudentService.updateQuiz(quizId = quizId,studentId = studentId,responses = responsesFromCache)
        }
        else{
            val finalResponses = responses.map { body ->
                val questionId = UUID.fromString(body["questionId"] as String)
                val question = questionRepository.findById(questionId)
                    .orElseThrow { IllegalArgumentException("Question not found") }
                val type = question.getQuestionType()
                when (type) {
                    QuestionTypes.MCQ -> objectMapper.convertValue(body, MCQResponseDTO::class.java)
                    QuestionTypes.CODING -> objectMapper.convertValue(body, CodingResponseDTO::class.java)
                    QuestionTypes.MMCQ -> objectMapper.convertValue(body, MMCQResponseDTO::class.java)
                    QuestionTypes.TRUEFALSE -> objectMapper.convertValue(body, TrueFalseResponseDTO::class.java)
                    QuestionTypes.DESCRIPTIVE -> objectMapper.convertValue(body, DescriptiveResponseDTO::class.java)
                    QuestionTypes.FILL_UP -> objectMapper.convertValue(body, FillUpResponseDTO::class.java)
                    QuestionTypes.MATCH_THE_FOLLOWING -> objectMapper.convertValue(body, MatchResponseDTO::class.java)
                    QuestionTypes.FILE_UPLOAD -> objectMapper.convertValue(body, FileUploadResponseDTO::class.java)

                    else -> throw IllegalArgumentException("Unknown question type")
                }}
            val final = quizStudentService.mapResponsesByQuestionId(finalResponses)
            quizStudentService.updateQuiz(
                quizId = quizId,
                studentId = studentId,
                responses = final
            )


        }

            }



    @PatchMapping("/updateCache")
    fun updateCache(@PathVariable quizId: UUID, @RequestBody body: Map<String, Any>){
        val studentId = getCurrentUserId()
        val questionId = UUID.fromString(body["questionId"] as String)
        val question = questionRepository.findById(questionId).orElseThrow { IllegalArgumentException("Question not found") }

        val type = question.getQuestionType()

        val answer = when (type) {
                QuestionTypes.MCQ -> objectMapper.convertValue(body, MCQResponseDTO::class.java)
                QuestionTypes.CODING -> objectMapper.convertValue(body, CodingResponseDTO::class.java)
                QuestionTypes.MMCQ -> objectMapper.convertValue(body, MMCQResponseDTO::class.java)
                QuestionTypes.TRUEFALSE -> objectMapper.convertValue(body, TrueFalseResponseDTO::class.java)
                QuestionTypes.DESCRIPTIVE -> objectMapper.convertValue(body, DescriptiveResponseDTO::class.java)
            QuestionTypes.FILL_UP -> objectMapper.convertValue(body, FillUpResponseDTO::class.java)
            QuestionTypes.MATCH_THE_FOLLOWING -> objectMapper.convertValue(body, MatchResponseDTO::class.java)
            QuestionTypes.FILE_UPLOAD -> objectMapper.convertValue(body, FileUploadResponseDTO::class.java)

            else -> throw IllegalArgumentException("Unknown question type")
        }
        quizCacheService.updateCache(quizId,studentId,answer)

    }

//    @PatchMapping("/save")
//    fun saveQuestion(@PathVariable quizId: UUID,answer:ResponseDTO){
//        val studentId = getCurrentUserId()
//        quizStudentService.saveQuestion(quizId,studentId,answer)
//
//    }

//    @PatchMapping("/submit")
//    fun submitQuiz(@PathVariable quizId: UUID, responses : List<ResponseDTO>? = null){
//        val studentId = getCurrentUserId()
//        if(responses == null)
//        {
//            val responses = quizCacheService.getAllAnswers(quizId = quizId,studentId = studentId)
//            quizStudentService.submitQuiz(quizId = quizId,studentId = studentId,responses = responses)
//        }
//        else{
//            quizStudentService.submitQuiz(quizId = quizId,studentId = studentId,responses = responses)
//        }
//
//    }
}