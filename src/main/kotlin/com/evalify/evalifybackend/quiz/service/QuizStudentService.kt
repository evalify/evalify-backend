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
import com.fasterxml.jackson.databind.ObjectMapper
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
        private val questionRepository: QuestionRepository,
    private val quizCleanupService: QuizCleanupService,
    private val objectMapper: ObjectMapper

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

        // 2. Check existing quiz student and validate student status  
        val existingQuizStudent = try {
            quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString())
        } catch (e: Exception) {
            // If reading existing student fails due to corrupted data, clear it and try again
            println("Error reading existing quiz student, clearing corrupted data and retrying: ${e.message}")
            quizCleanupService.clearCorruptedResponsesOnly(quizId, studentId)
            // Try to read again after cleanup
            try {
                quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString())
            } catch (retryError: Exception) {
                println("Error reading quiz student even after cleanup: ${retryError.message}")
                null
            }
        }

        if (existingQuizStudent != null) {
            println("Found existing quiz student record for student $studentId in quiz $quizId")
            
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
            val ipList = existingQuizStudent.ipAddress ?: mutableListOf()
            
            if (!ipList.contains(ipAddress)) {
                try {
                    // Use raw SQL to update IP addresses to avoid JPA serialization issues
                    val currentIpList = mutableListOf<String>().apply {
                        addAll(ipList)
                        add(ipAddress)
                    }
                    
                    // Build PostgreSQL array literal
                    val ipArrayLiteral = "{${currentIpList.joinToString(",") { "\"$it\"" }}}"
                    
                    val updateIpSql = """
                        UPDATE quiz_student 
                        SET ip_address = :ipArray::text[]
                        WHERE quiz_id = :quizId AND student_id = :studentId
                    """.trimIndent()
                    
                    val updateQuery = entityManager.createNativeQuery(updateIpSql)
                    updateQuery.setParameter("ipArray", ipArrayLiteral)
                    updateQuery.setParameter("quizId", quizId)
                    updateQuery.setParameter("studentId", studentId)
                    val rowsUpdated = updateQuery.executeUpdate()
                    
                    if (rowsUpdated > 0) {
                        println("Successfully updated IP address for student $studentId in quiz $quizId")
                    }
                } catch (sqlError: Exception) {
                    println("Error updating IP with raw SQL, trying JPA fallback: ${sqlError.message}")
                    // Fallback to JPA save
                    try {
                        ipList.add(ipAddress)
                        // Create a new instance to avoid entity state issues
                        val updatedStudent = QuizStudent(
                            id = existingQuizStudent.id,
                            quiz = existingQuizStudent.quiz,
                            student = existingQuizStudent.student,
                            startTime = existingQuizStudent.startTime,
                            duration = existingQuizStudent.duration,
                            endTime = existingQuizStudent.endTime,
                            isSubmitted = existingQuizStudent.isSubmitted,
                            violations = existingQuizStudent.violations,
                            isViolated = existingQuizStudent.isViolated,
                            ipAddress = ipList,
                            submitTime = existingQuizStudent.submitTime,
                            responses = mutableListOf(), // Reset responses to avoid serialization issues
                            results = existingQuizStudent.results,
                            setNumber = existingQuizStudent.setNumber
                        )
                        quizStudentRepository.save(updatedStudent)
                        println("Successfully updated IP address using JPA fallback")
                    } catch (jpaError: Exception) {
                        println("Both SQL and JPA IP update failed, but continuing: ${jpaError.message}")
                        // Don't fail the whole operation just because IP update failed
                        // The quiz can still proceed with the existing IP list
                    }
                }
            }

            // 3. Try to get cached questions after all validations
            val cachedQuestions = quizCacheService.getCachedQuizQuestions(quizId, studentId)
            if (cachedQuestions != null) {
                return cachedQuestions
            }
        } else {
            println("No existing quiz student found, creating new record for student $studentId in quiz $quizId")
            
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

            // Create new quiz student with race condition protection
            try {
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
                println("Successfully created new quiz student record for student $studentId in quiz $quizId")
            } catch (duplicateError: Exception) {
                // Handle duplicate key error - student might have been created by another request
                if (duplicateError.message?.contains("duplicate key") == true || 
                    duplicateError.message?.contains("unique constraint") == true ||
                    duplicateError.message?.contains("violates unique constraint") == true) {
                    println("Duplicate key error when creating quiz student - record already exists. Checking existing record.")
                    
                    // Double-check: try to find the existing record that was created by another request
                    val concurrentlyCreatedStudent = try {
                        quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString())
                    } catch (e: Exception) {
                        println("Error finding concurrently created student: ${e.message}")
                        null
                    }
                    
                    if (concurrentlyCreatedStudent != null) {
                        println("Found concurrently created student record, updating IP address if needed")
                        // Update IP address if the current IP is not in the list
                        val existingIps = concurrentlyCreatedStudent.ipAddress ?: mutableListOf()
                        if (!existingIps.contains(ipAddress)) {
                            try {
                                existingIps.add(ipAddress)
                                quizStudentRepository.save(concurrentlyCreatedStudent)
                                println("Successfully updated IP address for concurrently created record")
                            } catch (updateError: Exception) {
                                println("Error updating IP for concurrent record, but continuing: ${updateError.message}")
                            }
                        }
                    } else {
                        println("Could not find the concurrently created record, but continuing anyway")
                    }
                    
                    // Don't throw error, just continue - the record exists now
                } else {
                    println("Error creating new quiz student: ${duplicateError.message}")
                    throw duplicateError
                }
            }
        }

        val student = try {
            quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString()) 
                ?: throw NotFoundException("QuizStudent record not found")
        } catch (e: Exception) {
            // If reading student fails due to corrupted data, clear it and try again
            println("Error reading quiz student due to corrupted data, clearing and retrying: ${e.message}")
            quizCleanupService.clearCorruptedResponsesOnly(quizId, studentId)
            quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString()) 
                ?: throw NotFoundException("QuizStudent record not found after cleanup")
        }

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
            quizCleanupService.clearCorruptedResponsesOnly(quizId, studentId)
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

    fun submitQuiz(quizId: UUID, studentId: String?, responses: Map<UUID, ResponseDTO>) {
        val quizStudent = try {
            quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId ?: "")
                ?: throw NotFoundException("Quiz with id $quizId not found")
        } catch (e: Exception) {
            // If there's a serialization error when reading the entity, clear corrupted data first
            println("Database serialization error in updateQuiz, clearing corrupted data: ${e.message}")
            quizCleanupService.clearCorruptedResponsesOnly(quizId, studentId)
            // Try again after cleanup
            quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId ?: "")
                ?: throw NotFoundException("Quiz with id $quizId not found")
        }

        // Check if we have cached responses or if there are new responses to process
        val hasNewResponses = responses.isNotEmpty()
        val hasCachedResponses = try {
            quizStudent.responses != null
        } catch (e: Exception) {
            false // If there's an error reading cached responses, treat as no cache
        }

        // Only process responses if we have cached data OR new responses to save
        val existingResponses: MutableList<StudentResponseDTO>? = if (hasNewResponses || hasCachedResponses) {
            val currentResponses = try {
                quizStudent.responses ?: mutableListOf()
            } catch (e: Exception) {
                println("Error reading existing responses, starting with empty list: ${e.message}")
                mutableListOf()
            }

            responses.forEach { (questionId, newResponse) ->
                val index = currentResponses.indexOfFirst { it.questionId == questionId }
                val finalResponse = mapToResponseType(questionId, newResponse)

                if (index != -1) {
                    currentResponses[index] = finalResponse
                } else {
                    currentResponses.add(finalResponse)
                }
            }

            currentResponses
        } else {
            // Don't touch responses field if no cache and no new responses
            null
        }

        // Set the responses back to the entity only if we processed them
        if (existingResponses != null) {
            quizStudent.responses = existingResponses
        }
        // Update submission status and time
        val currentTime = Instant.now()
        try {
            // Use raw SQL to update submission fields to avoid JPA serialization issues
            val updateSql = if (existingResponses != null) {
                // Update responses along with submission status
                """
                UPDATE quiz_student 
                SET responses = :responses::jsonb, 
                    is_submitted = true, 
                    submit_time = :submitTime
                WHERE quiz_id = :quizId AND student_id = :studentId
                """.trimIndent()
            } else {
                // Only update submission status, don't touch responses field
                """
                UPDATE quiz_student 
                SET is_submitted = true, 
                    submit_time = :submitTime
                WHERE quiz_id = :quizId AND student_id = :studentId
                """.trimIndent()
            }
            
            val updateQuery = entityManager.createNativeQuery(updateSql)
            if (existingResponses != null) {
                updateQuery.setParameter("responses", objectMapper.writeValueAsString(existingResponses))
            }
            updateQuery.setParameter("submitTime", currentTime)
            updateQuery.setParameter("quizId", quizId)
            updateQuery.setParameter("studentId", studentId)
            val rowsUpdated = updateQuery.executeUpdate()
            
            if (rowsUpdated == 0) {
                throw IllegalStateException("No quiz student record found to update")
            }
            
            println("Quiz submitted successfully for student $studentId in quiz $quizId at $currentTime")
        } catch (e: Exception) {
            println("Error updating submission status with raw SQL, falling back to JPA: ${e.message}")
            // Fallback to JPA save if SQL fails - but this time manually set the submission fields
            try {
                // Manually update the fields in the entity before saving
                val updatedQuizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId ?: "")
                if (updatedQuizStudent != null) {
                    // Create a new instance with updated fields to avoid JPA issues
                    val newQuizStudent = QuizStudent(
                        id = updatedQuizStudent.id,
                        quiz = updatedQuizStudent.quiz,
                        student = updatedQuizStudent.student,
                        startTime = updatedQuizStudent.startTime,
                        duration = updatedQuizStudent.duration,
                        endTime = updatedQuizStudent.endTime,
                        isSubmitted = true,  // Set to true
                        violations = updatedQuizStudent.violations,
                        isViolated = updatedQuizStudent.isViolated,
                        ipAddress = updatedQuizStudent.ipAddress,
                        submitTime = currentTime,  // Set current time
                        responses = existingResponses ?: updatedQuizStudent.responses, // Only update if we processed responses
                        results = updatedQuizStudent.results,
                        setNumber = updatedQuizStudent.setNumber


                    )
                    quizStudentRepository.save(newQuizStudent)
                    println("Fallback JPA save successful for quiz submission")
                } else {
                    throw IllegalStateException("Quiz student record not found for fallback save")
                }
            } catch (fallbackError: Exception) {
                println("Fallback JPA save also failed: ${fallbackError.message}")
                throw IllegalStateException("Failed to update quiz submission status", fallbackError)
            }
        }
    }


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
            // Only clear the responses JSONB field - leave other fields alone
            val sql = """
                UPDATE quiz_student 
                SET responses = '[]'::jsonb
                WHERE quiz_id = :quizId AND student_id = :studentId
            """.trimIndent()
            
            val query = entityManager.createNativeQuery(sql)
            query.setParameter("quizId", quizId)
            query.setParameter("studentId", studentId)
            val rowsUpdated = query.executeUpdate()
            
            println("Cleared corrupted responses data for quiz $quizId, student $studentId. Rows updated: $rowsUpdated")
        } catch (e: Exception) {
            println("Error clearing corrupted data in new transaction: ${e.message}")
            // If SQL fails, try to delete and recreate the record as last resort
            try {
                val deleteSql = "DELETE FROM quiz_student WHERE quiz_id = :quizId AND student_id = :studentId"
                val deleteQuery = entityManager.createNativeQuery(deleteSql)
                deleteQuery.setParameter("quizId", quizId)
                deleteQuery.setParameter("studentId", studentId)
                deleteQuery.executeUpdate()
                println("Deleted corrupted quiz student record as last resort")
            } catch (deleteError: Exception) {
                println("Even delete failed: ${deleteError.message}")
            }
        }
    }




}