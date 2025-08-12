package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizInfoDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizQuestionResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizStudentInfoDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.MatchPairResponse
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentCodingResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentDescriptiveResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentFileUploadResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentFillUpResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentMCQResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentMMCQResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentMatchResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentTrueFalseResponseDTO
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizSet
import com.evalify.evalifybackend.quiz.domain.QuizSetQuestion
import com.evalify.evalifybackend.quiz.domain.QuizStudent
import com.evalify.evalifybackend.quiz.domain.QuizTags
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizSetRepository
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.section.domain.DTO.GetSectionDTO
import com.evalify.evalifybackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import java.time.Instant
import java.util.*
import kotlin.random.Random

@Service
@Transactional
class QuizStudentService(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository,
    private val quizStudentRepository: QuizStudentRepository,
    private val quizSetRepository: QuizSetRepository,
    private val redisTemplate: RedisTemplate<String, QuizQuestionsReturnDTO>,
    private val responseMapRedisTemplate: RedisTemplate<String, ResponseDTO>,
    private val passwordEncoder: PasswordEncoder,
    private val questionRepository: QuestionRepository,
    //private val quizCacheService: QuizCacheService,
) {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    fun distributeQuestions(quiz: Quiz, quizSets: List<QuizSet>): List<QuizSetQuestion> {
        // If there's only one set, return its questions
        if (quiz.noOfSets == 1 && quizSets.isNotEmpty()) {
            return quizSets[0].questions.toList()
        }

        // For multiple sets, randomly select one set
        val quizNo = Random.nextInt(0, quiz.noOfSets)
        return quizSets.find { it.setNumber == quizNo }?.questions ?: emptyList()
    }
    fun mapToResponseList(responseMap: Map<UUID, ResponseDTO>): List<ResponseDTO> {
        return responseMap.values.toList()
    }


    fun startQuiz(
        quizId: UUID,
        studentId: String?,
        ipAddress: String,
        requestTime: Instant,
        password: String? = null,
        quizCacheService: QuizCacheService
    ): QuizQuestionReturnDTO {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { NotFoundException("Quiz with id $quizId not found") }

        val user = if(studentId != null) {
            userRepository.findById(studentId).orElseThrow { NotFoundException("User with id $studentId not found") }
        } else{
            throw NotFoundException("Student id cannot be null. Please provide a valid student id.")
        }

        // 1. First validate time constraints
        if (requestTime.isBefore(quiz.startTime)) {
            return QuizQuestionReturnDTO(
                quizTags = quiz.quizTags.map { tags ->
                    QuizTagsReturnDTO(
                        tags.id,
                        tags.name,
                        tags.description
                    )
                },
                questions = emptyList(),
                message = "Quiz has not started yet.",
                quizInfo = QuizInfoDTO(
                    quizId = quiz.id,
                    quizName = quiz.name,
                    calculator = quiz.calculator,
                    kioskMode = quiz.kioskMode,
                    fullScreen = quiz.fullScreen,
                    autoSubmit = quiz.autoSubmit,
                    linearQuiz = quiz.linearQuiz
                )

            )
        }
        if (requestTime.isAfter(quiz.endTime)) {
            return QuizQuestionReturnDTO(
                quizTags = quiz.quizTags.map { tags ->
                    QuizTagsReturnDTO(
                        tags.id,
                        tags.name,
                        tags.description
                    )
                },
                quizInfo = QuizInfoDTO(
                    quizId = quiz.id,
                    quizName = quiz.name,
                    calculator = quiz.calculator,
                    kioskMode = quiz.kioskMode,
                    fullScreen = quiz.fullScreen,
                    autoSubmit = quiz.autoSubmit,
                    linearQuiz = quiz.linearQuiz
                ),
                questions = emptyList(),
                message = "Quiz has ended."
            )
        }

        val existingQuizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString())

        if (existingQuizStudent != null) {
            // Check if quiz is already submitted
            if (existingQuizStudent.isSubmitted) {
                return QuizQuestionReturnDTO(
                    quizTags = quiz.quizTags.map { tags ->
                        QuizTagsReturnDTO(
                            tags.id,
                            tags.name,
                            tags.description
                        )
                    },
                    quizInfo = QuizInfoDTO(
                        quizId = quiz.id,
                        quizName = quiz.name,
                        calculator = quiz.calculator,
                        kioskMode = quiz.kioskMode,
                        fullScreen = quiz.fullScreen,
                        autoSubmit = quiz.autoSubmit,
                        linearQuiz = quiz.linearQuiz
                    ),
                    questions = emptyList(),
                    message = "Quiz has already been submitted."
                )
            }

            // Update IP address if needed
            if (!existingQuizStudent.ipAddress.contains(ipAddress)) {
                existingQuizStudent.ipAddress.add(ipAddress)
                quizStudentRepository.save(existingQuizStudent)
            }

            // 3. Try to get cached questions after all validations
            val cachedQuestions = quizCacheService.getCachedQuizQuestions(quizId, studentId)
            if (cachedQuestions != null) {
                return cachedQuestions
            }
        } else {
            // 4. Validate password only when creating new quiz student
            if (quiz.password != null && password != quiz.password) {
                return QuizQuestionReturnDTO(
                    quizTags = quiz.quizTags.map { tags ->
                        QuizTagsReturnDTO(
                            tags.id,
                            tags.name,
                            tags.description
                        )
                    },
                    questions = emptyList(),
                    quizInfo = QuizInfoDTO(
                        quizId = quiz.id,
                        quizName = quiz.name,
                        calculator = quiz.calculator,
                        kioskMode = quiz.kioskMode,
                        fullScreen = quiz.fullScreen,
                        autoSubmit = quiz.autoSubmit,
                        linearQuiz = quiz.linearQuiz
                    ),
                    message = "Wrong password."
                )
            }

            // Create new quiz student
            val newStudent = quizStudentRepository.save(
                QuizStudent(
                    quiz = quiz,
                    student = user,
                    isSubmitted = false,
                    startTime = Instant.now(),
                    duration = quiz.duration,
                    endTime = quiz.endTime,
                    ipAddress = mutableListOf(ipAddress)
                )
            )
        }

        val student = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString()) ?: throw NotFoundException("QuizStudent record not found")

        // 5. Generate questions from the database
        val questions = generateQuizQuestions(quiz,student ,quizId,studentId,quizCacheService)

        // 6. Store in the cache for future use
        quizCacheService.storeStudentQuestions(quizId, studentId, questions.questions.map{sample ->
            sample.question
        })

        return questions
    }
//    fun getAllAnswers(quizId: UUID, studentId: String?): List<ResponseDTO> {
//        // First try to get from Redis cache
//        val qId = quizId.toString()
//        val key = "quiz:$qId:student:$studentId:answers"
//        val hashOps = responseMapRedisTemplate.opsForHash<UUID, ResponseDTO>()
//        val cachedAnswers = hashOps.values(key).toList()
//
//        if (cachedAnswers.isNotEmpty()) {
//            return cachedAnswers
//        }
//
//        // If cache is empty, try to get from database with error handling
//        try {
//            val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId ?: "")
//            return if (quizStudent?.responses != null) {
//                convertStudentResponsesToResponseDTOs(quizStudent.responses)
//            } else {
//                emptyList()
//            }
//        } catch (e: Exception) {
//            // If there's a serialization error, clear the corrupted data and return empty list
//            println("Database serialization error in getAllAnswers, clearing corrupted data: ${e.message}")
//            clearCorruptedQuizStudentData(quizId, studentId)
//            return emptyList()
//        }
//    }
    
    private fun convertStudentResponsesToResponseDTOs(studentResponses: List<StudentResponseDTO>): List<ResponseDTO> {
        return studentResponses.mapNotNull { studentResponse ->
            try {
                when (studentResponse) {
                    is StudentDescriptiveResponseDTO -> ResponseDTO(
                        questionId = studentResponse.questionId,
                        duration = studentResponse.duration,
                        stringAnswer = studentResponse.answer
                    )
                    is StudentMCQResponseDTO -> ResponseDTO(
                        questionId = studentResponse.questionId,
                        duration = studentResponse.duration,
                        uuidAnswer = studentResponse.answer
                    )
                    is StudentMMCQResponseDTO -> ResponseDTO(
                        questionId = studentResponse.questionId,
                        duration = studentResponse.duration,
                        listUUIDAnswer = studentResponse.answer
                    )
                    is StudentTrueFalseResponseDTO -> ResponseDTO(
                        questionId = studentResponse.questionId,
                        duration = studentResponse.duration,
                        booleanAnswer = studentResponse.answer
                    )
                    is StudentFillUpResponseDTO -> ResponseDTO(
                        questionId = studentResponse.questionId,
                        duration = studentResponse.duration,
                        fillupAnswer = studentResponse.answer
                    )
                    is StudentCodingResponseDTO -> ResponseDTO(
                        questionId = studentResponse.questionId,
                        duration = studentResponse.duration,
                        stringAnswer = studentResponse.answer
                    )
                    is StudentFileUploadResponseDTO -> ResponseDTO(
                        questionId = studentResponse.questionId,
                        duration = studentResponse.duration,
                        stringAnswer = studentResponse.answer
                    )
                    is StudentMatchResponseDTO -> ResponseDTO(
                        questionId = studentResponse.questionId,
                        duration = studentResponse.duration,
                        matchAnswer = studentResponse.answer
                    )
                    else -> null
                }
            } catch (e: Exception) {
                // Log the error and skip this response
                println("Error converting StudentResponseDTO to ResponseDTO: ${e.message}")
                null
            }
        }
    }

    private fun generateQuizQuestions(quiz: Quiz,existingStudent : QuizStudent?,qId: UUID,studentId: String,quizCacheService: QuizCacheService): QuizQuestionReturnDTO {
        // Get all quiz sets for this quiz
        val quizSets = quizSetRepository.findByQuiz(quiz) ?: emptyList()
        
        // Use cache-only responses to avoid database serialization issues
        val responses = quizCacheService.getAllAnswers(qId,studentId)
        val newResponses = mapToResponseList(responses)


        if (quizSets.isEmpty())
            throw NotFoundException("Quiz with id ${quiz.id} has no quiz sets.")

        // Distribute questions according to quiz settings
        val selectedQuestions = distributeQuestions(quiz, quizSets)

        // Map questions to DTO
        val questions = selectedQuestions.map { question ->
            QuizQuestionsReturnDTO(
                questions = question.question.question.mapToType(),
                section = GetSectionDTO(
                    id = question.question.section.id,
                    name = question.question.section.name
                ),
                type = question.question.question.getQuestionType()
            )
        }
        if (quiz.shuffleQuestions == true) questions.shuffled() else questions

        val questionResponse = questions.map { question ->
            QuizQuestionResponseDTO(
                question = question,
                response = newResponses.find { response -> response.questionId == question.questions.questionId }

            )
        }

        // Create final response
        return QuizQuestionReturnDTO(
            quizTags = quiz.quizTags.map { tags ->
                QuizTagsReturnDTO(
                    id = tags.id,
                    name = tags.name,
                    description = tags.description
                )
            },
            // Only shuffle if quiz settings allow it
            questions = questionResponse,
            message = "Quiz has started successfully.",
            quizInfo = QuizInfoDTO(
                quizId = quiz.id,
                quizName = quiz.name,
                calculator = quiz.calculator,
                kioskMode = quiz.kioskMode,
                fullScreen = quiz.fullScreen,
                autoSubmit = quiz.autoSubmit,
                linearQuiz = quiz.linearQuiz
            ),
            quizStudentInfo = QuizStudentInfoDTO(
                duration = existingStudent?.duration,
                endTime = existingStudent?.endTime,
                startTime = existingStudent?.startTime,
                violations = existingStudent?.violations,
                isViolated = existingStudent?.isViolated



            )

        )
    }

//    fun saveQuestion(quizId: UUID, studentId: String?, answer: ResponseDTO) {
//        val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString() ?: "")
//            ?: throw NotFoundException("QuizStudent record not found")
//        val responses = quizStudent.responses
//        val existingIndex = responses.indexOfFirst { it.questionId == answer.questionId }
//        if (existingIndex != -1) {
//            responses[existingIndex] = answer
//        } else {
//            responses.add(answer)
//        }
//        quizStudentRepository.save(quizStudent)
//    }


    fun mapResponsesByQuestionId(responses: List<ResponseDTO>): Map<UUID, ResponseDTO> {
        return responses.associateBy { it.questionId }
    }

    fun updateQuiz(quizId: UUID, studentId: String?, responses: Map<UUID, ResponseDTO>) {
        val quizStudent = try {
            quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId ?: "")
                ?: throw NotFoundException("Quiz with id $quizId not found")
        } catch (e: Exception) {
            // If there's a serialization error when reading the entity, clear corrupted data first
            println("Database serialization error in updateQuiz, clearing corrupted data: ${e.message}")
            clearCorruptedQuizStudentData(quizId, studentId)
            // Try again after cleanup
            quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId ?: "")
                ?: throw NotFoundException("Quiz with id $quizId not found")
        }

        // Ensure responses list is not null - start with empty list if corrupted data was cleared
        val existingResponses : MutableList<StudentResponseDTO> = try {
            quizStudent.responses ?: mutableListOf()
        } catch (e: Exception) {
            println("Error reading existing responses, starting with empty list: ${e.message}")
            mutableListOf()
        }

        responses.forEach { (questionId, newResponse) ->
            val index = existingResponses.indexOfFirst { it.questionId == questionId }
            val finalResponse = mapToResponseType(questionId, newResponse)

            if (index != -1) {
                existingResponses[index] = finalResponse
            } else {
                existingResponses.add(finalResponse)
            }
        }

        // Set the responses back to the entity
        quizStudent.responses = existingResponses
        quizStudentRepository.save(quizStudent)
    }

    fun mapToResponseType(questionId: UUID, response: ResponseDTO): StudentResponseDTO {
        val question = questionRepository.findById(questionId).orElseThrow { NotFoundException("Question with id $questionId not found") }
        val type = question.getQuestionType()
        
        try {
            val finalResponse : StudentResponseDTO = when(type){
                QuestionTypes.FILL_UP ->
                    StudentFillUpResponseDTO(
                        questionId = response.questionId,
                        duration = response.duration,
                        answer = response.fillupAnswer
                    )
                QuestionTypes.MCQ ->
                    StudentMCQResponseDTO(
                        questionId = response.questionId,
                        duration = response.duration,
                        answer = response.uuidAnswer

                    )
                QuestionTypes.DESCRIPTIVE ->
                    StudentDescriptiveResponseDTO(
                        questionId = response.questionId,
                        duration = response.duration,
                        answer = response.stringAnswer
                    )
                QuestionTypes.TRUEFALSE ->
                    StudentTrueFalseResponseDTO(
                        questionId = response.questionId,
                        duration = response.duration,
                        answer = response.booleanAnswer
                    )
                QuestionTypes.FILE_UPLOAD ->
                    StudentFileUploadResponseDTO(
                        questionId = response.questionId,
                        duration = response.duration,
                        answer = response.stringAnswer
                    )
                QuestionTypes.MMCQ ->
                    StudentMMCQResponseDTO(
                        questionId = response.questionId,
                        duration = response.duration,
                        answer = response.listUUIDAnswer
                    )
                QuestionTypes.CODING ->
                    StudentCodingResponseDTO(
                        questionId = response.questionId,
                        duration = response.duration,
                        answer = response.stringAnswer
                    )
                QuestionTypes.MATCH_THE_FOLLOWING ->
                    StudentMatchResponseDTO(
                        questionId = response.questionId,
                        duration = response.duration,
                        answer = response.matchAnswer
                    )
                else -> throw Exception("Invalid question type: $type")

            }
            return finalResponse
        } catch (e: Exception) {
            // Enhanced error logging
            println("Error mapping ResponseDTO to StudentResponseDTO for question $questionId of type $type: ${e.message}")
            println("ResponseDTO: questionId=${response.questionId}, duration=${response.duration}, stringAnswer=${response.stringAnswer}")
            throw Exception("Failed to map response for question $questionId: ${e.message}", e)
        }
    }

//    fun submitQuiz(quizId: UUID, studentId: String?, responses: List<ResponseDTO>) {
//        val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString() ?: "")
//            ?: throw NotFoundException("Quiz with id $quizId not found")
//
//        val existingResponses = quizStudent.responses
//
//        responses.forEach { newResponse ->
//            val index = existingResponses.indexOfFirst { it.questionId == newResponse.questionId }
//            val finalResponse = mapToResponseType(questionId, newResponse)
//
//            if (index != -1) {
//                existingResponses[index] = newResponse
//            } else {
//                existingResponses.add(newResponse)
//            }
//        }
//
//
//        quizStudentRepository.save(quizStudent)
//    }

    fun getQuizTags(quizId: UUID): List<QuizTags> {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { NotFoundException("Quiz with id $quizId not found") }
        return quiz.quizTags
    }

    fun clearCorruptedQuizStudentData(quizId: UUID, studentId: String?) {
        try {
            // Use raw SQL to bypass JPA deserialization issues
            val sql = """
                UPDATE quiz_student 
                SET responses = '[]'::jsonb 
                WHERE quiz_id = :quizId AND student_id = :studentId
            """.trimIndent()
            
            val query = entityManager.createNativeQuery(sql)
            query.setParameter("quizId", quizId)
            query.setParameter("studentId", studentId)
            val rowsUpdated = query.executeUpdate()
            
            println("Cleared corrupted data for quiz $quizId, student $studentId. Rows updated: $rowsUpdated")
        } catch (e: Exception) {
            println("Error clearing corrupted quiz student data with SQL: ${e.message}")
        }
    }

    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    fun clearCorruptedDataInNewTransaction(quizId: UUID, studentId: String?) {
        try {
            // Only clear the responses field, not the entire record
            val sql = """
                UPDATE quiz_student 
                SET responses = '[]'::jsonb 
                WHERE quiz_id = :quizId AND student_id = :studentId
                  AND responses IS NOT NULL
            """.trimIndent()
            
            val query = entityManager.createNativeQuery(sql)
            query.setParameter("quizId", quizId)
            query.setParameter("studentId", studentId)
            val rowsUpdated = query.executeUpdate()
            
            // Also ensure ipAddress is not null by updating it to empty array if null
            val ipFixSql = """
                UPDATE quiz_student 
                SET ip_address = '[]'::jsonb 
                WHERE quiz_id = :quizId AND student_id = :studentId
                  AND ip_address IS NULL
            """.trimIndent()
            
            val ipQuery = entityManager.createNativeQuery(ipFixSql)
            ipQuery.setParameter("quizId", quizId)
            ipQuery.setParameter("studentId", studentId)
            val ipRowsUpdated = ipQuery.executeUpdate()
            
            println("Cleared corrupted data in new transaction for quiz $quizId, student $studentId. Responses rows updated: $rowsUpdated, IP rows updated: $ipRowsUpdated")
        } catch (e: Exception) {
            println("Error clearing corrupted data in new transaction: ${e.message}")
        }
    }




}