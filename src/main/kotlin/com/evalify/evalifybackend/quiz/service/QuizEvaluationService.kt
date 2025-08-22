package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.evaluation.SaveGradeDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentCodingResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentDescriptiveResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentFileUploadResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentFillUpResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentMCQResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentMMCQResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentMatchResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentTrueFalseResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.BlankResponseDTO
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.quiz.repository.QuizEvaluationRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class QuizEvaluationService(
    private val quizEvaluationRepository: QuizEvaluationRepository,
    private val objectMapper: ObjectMapper,
    private val questionRepository: com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
) {
    private val logger = LoggerFactory.getLogger(QuizEvaluationService::class.java)

    @Transactional
    fun saveGrades(quizId: UUID, studentId: String, grades: List<SaveGradeDTO>) {

        
        // Validation
        if (grades.isEmpty()) {
            throw IllegalArgumentException("Grades list cannot be empty")
        }

        // Check for duplicate question IDs
        val duplicateQuestions = grades.groupBy { it.questionId }
            .filter { it.value.size > 1 }
            .keys

        if (duplicateQuestions.isNotEmpty()) {
            throw IllegalArgumentException("Duplicate grades found for questions: ${duplicateQuestions.joinToString(", ")}")
        }

        // Step 1: Check if quiz student record exists
        val exists = try {
            val result = quizEvaluationRepository.existsByQuizIdAndStudentId(quizId, studentId)
            logger.debug("Quiz student exists = {}", result)
            result
        } catch (e: Exception) {
            logger.debug("Failed to check if quiz student exists: {}", e.message)
            throw IllegalStateException("Database error while checking quiz student existence", e)
        }

        if (!exists) {
            throw NotFoundException("Quiz student record not found for quiz $quizId and student $studentId")
        }

        // Step 2: Check submission status
        val isSubmitted = try {
            val result = quizEvaluationRepository.getSubmissionStatus(quizId, studentId)
            logger.debug("Submission status = {}", result)
            result ?: false
        } catch (e: Exception) {
            logger.debug("Failed to check submission status: {}", e.message)
            false
        }

        // Enforce that quiz must be submitted before grading
        if (!isSubmitted) {
            throw IllegalStateException("Cannot grade quiz that has not been submitted yet. Quiz ID: $quizId, Student ID: $studentId")
        }

        // Step 3: Get existing responses as JSON
        val responsesJson = try {
            val result = quizEvaluationRepository.getResponsesAsJson(quizId, studentId)
            logger.debug("Raw responses JSON length = {}", result?.length ?: 0)
            if (result != null && result.length < 1000) {
                logger.debug("Raw responses JSON content = {}", result)
            } else if (result != null) {
                logger.debug("Raw responses JSON preview = {}...", result.take(500))
            }
            result
        } catch (e: Exception) {
            logger.debug("Failed to get responses JSON: {}", e.message)
            e.printStackTrace()
            throw IllegalStateException("Cannot read responses for grading", e)
        }

        if (responsesJson.isNullOrBlank()) {
            throw IllegalStateException("No responses found for grading. Student may not have attempted the quiz.")
        }

        // Step 4: Parse responses from JSON
        val existingResponses: MutableList<StudentResponseDTO> = try {
            logger.debug("Attempting to parse JSON with ObjectMapper...")
            
            // Parse as raw JsonNode first to determine response types
            val jsonArray = objectMapper.readTree(responsesJson)
            val responsesList = mutableListOf<StudentResponseDTO>()
            
            jsonArray.forEach { responseNode ->
                val questionId = UUID.fromString(responseNode.get("questionId").asText())
                val marksNode = responseNode.get("marks")
                val marks: Float? = if (marksNode == null || marksNode.isNull) null else marksNode.floatValue()

                val remarks = if (responseNode.get("remarks").isNull) null else responseNode.get("remarks").asText()
                val duration = responseNode.get("duration").asLong()
                val isEvaluated = responseNode.get("isEvaluated").asBoolean()
                
                // Get question type from database to determine correct DTO type
                val question = questionRepository.findById(questionId).orElseThrow { 
                    NotFoundException("Question with id $questionId not found") 
                }
                val questionType = question.getQuestionType()
                
                // Create appropriate DTO based on question type
                val studentResponse: StudentResponseDTO = when (questionType) {
                    QuestionTypes.MCQ -> {
                        val answer = if (responseNode.has("answer") && !responseNode.get("answer").isNull) {
                            UUID.fromString(responseNode.get("answer").asText())
                        } else null
                        StudentMCQResponseDTO(
                            questionId = questionId,
                            answer = answer,
                            duration = duration,
                            marks = marks,
                            remarks = remarks,
                            isEvaluated = isEvaluated
                        )
                    }
                    QuestionTypes.MMCQ -> {
                        val answer = if (responseNode.has("answer") && responseNode.get("answer").isArray) {
                            responseNode.get("answer").map { UUID.fromString(it.asText()) }
                        } else null
                        StudentMMCQResponseDTO(
                            questionId = questionId,
                            answer = answer,
                            duration = duration,
                            marks = marks,
                            remarks = remarks,
                            isEvaluated = isEvaluated
                        )
                    }
                    QuestionTypes.TRUEFALSE -> {
                        val answer = if (responseNode.has("answer") && !responseNode.get("answer").isNull) {
                            responseNode.get("answer").asBoolean()
                        } else null
                        StudentTrueFalseResponseDTO(
                            questionId = questionId,
                            answer = answer,
                            duration = duration,
                            marks = marks,
                            remarks = remarks,
                            isEvaluated = isEvaluated
                        )
                    }
                    QuestionTypes.DESCRIPTIVE -> {
                        val answer = if (responseNode.has("answer") && !responseNode.get("answer").isNull) {
                            responseNode.get("answer").asText()
                        } else null
                        StudentDescriptiveResponseDTO(
                            questionId = questionId,
                            answer = answer,
                            duration = duration,
                            marks = marks,
                            remarks = remarks,
                            isEvaluated = isEvaluated
                        )
                    }
                    QuestionTypes.FILL_UP -> {
                        val answer = if (responseNode.has("answer") && responseNode.get("answer").isArray) {
                            responseNode.get("answer").map { blankNode ->
                                objectMapper.treeToValue(blankNode, BlankResponseDTO::class.java)
                            }
                        } else null
                        StudentFillUpResponseDTO(
                            questionId = questionId,
                            answer = answer,
                            duration = duration,
                            marks = marks,
                            remarks = remarks,
                            isEvaluated = isEvaluated
                        )
                    }
                    QuestionTypes.CODING -> {
                        val answer = if (responseNode.has("answer") && !responseNode.get("answer").isNull) {
                            responseNode.get("answer").asText()
                        } else null
                        StudentCodingResponseDTO(
                            questionId = questionId,
                            answer = answer,
                            duration = duration,
                            marks = marks,
                            remarks = remarks,
                            isEvaluated = isEvaluated
                        )
                    }
                    QuestionTypes.FILE_UPLOAD -> {
                        val answer = if (responseNode.has("answer") && !responseNode.get("answer").isNull) {
                            responseNode.get("answer").asText()
                        } else null
                        StudentFileUploadResponseDTO(
                            questionId = questionId,
                            answer = answer,
                            duration = duration,
                            marks = marks,
                            remarks = remarks,
                            isEvaluated = isEvaluated
                        )
                    }
                    QuestionTypes.MATCH_THE_FOLLOWING -> {
                        val answer = if (responseNode.has("answer") && responseNode.get("answer").isArray) {
                            responseNode.get("answer").map { matchNode ->
                                objectMapper.treeToValue(matchNode, com.evalify.evalifybackend.quiz.domain.DTO.responses.MatchPairResponse::class.java)
                            }
                        } else null
                        StudentMatchResponseDTO(
                            questionId = questionId,
                            answer = answer,
                            duration = duration,
                            marks = marks,
                            remarks = remarks,
                            isEvaluated = isEvaluated
                        )
                    }
                    else -> {
                        throw IllegalStateException("Unsupported question type: $questionType for question $questionId")
                    }
                }
                
                responsesList.add(studentResponse)
                logger.debug("Parsed response for question {} as {}", questionId, studentResponse.javaClass.simpleName)
            }
            
            logger.debug("Successfully parsed {} responses", responsesList.size)
            responsesList
        } catch (e: Exception) {
            logger.debug("Failed to parse responses JSON: {}", e.message)
            logger.debug("Exception type: {}", e.javaClass.simpleName)
            e.printStackTrace()
            
            // Try to parse as raw JSON to see structure
            try {
                val rawJson = objectMapper.readTree(responsesJson)
                logger.debug("Raw JSON structure: {}", rawJson)
                if (rawJson.isArray && rawJson.size() > 0) {
                    logger.debug("First element: {}", rawJson[0])
                }
            } catch (jsonE: Exception) {
                logger.debug("Cannot even parse as raw JSON: {}", jsonE.message)
            }
            
            throw IllegalStateException("Cannot parse responses for grading", e)
        }

        // Step 5: Validate that all question IDs exist in responses
        val missingQuestions = grades.filter { gradeDTO ->
            existingResponses.none { it.questionId == gradeDTO.questionId }
        }.map { it.questionId }

        if (missingQuestions.isNotEmpty()) {
            logger.debug("Missing questions: {}", missingQuestions)
            throw NotFoundException("Responses not found for questions: ${missingQuestions.joinToString(", ")}")
        }

        // Step 6: Update responses with grades
        grades.forEach { gradeDTO ->
            val index = existingResponses.indexOfFirst { it.questionId == gradeDTO.questionId }
            if (index != -1) {
                val existingResponse = existingResponses[index]
                val updatedResponse = updateResponseWithGrade(existingResponse, gradeDTO)
                existingResponses[index] = updatedResponse
                logger.debug("Updated grade for question {}: marks={}", gradeDTO.questionId, gradeDTO.marks)
            }
        }

        // Step 7: Save updated responses
        val updatedJson = try {
            objectMapper.writeValueAsString(existingResponses)
        } catch (e: Exception) {
            logger.debug("Failed to serialize updated responses: {}", e.message)
            throw IllegalStateException("Cannot serialize updated responses", e)
        }

        val rowsUpdated = try {
            val result = quizEvaluationRepository.updateResponsesWithGrades(quizId, studentId, updatedJson)
            logger.debug("Rows updated = {}", result)
            result
        } catch (e: Exception) {
            logger.debug("Failed to save updated responses: {}", e.message)
            throw IllegalStateException("Failed to save grades", e)
        }

        if (rowsUpdated == 0) {
            throw IllegalStateException("No records were updated. Quiz student may not exist.")
        }

        logger.debug("{} grades saved successfully for quiz {} and student {}", grades.size, quizId, studentId)
    }

    private fun updateResponseWithGrade(response: StudentResponseDTO, gradeDTO: SaveGradeDTO): StudentResponseDTO {
        return when (response) {
            is StudentMCQResponseDTO ->
                response.copy(
                    marks = gradeDTO.marks,
                    remarks = gradeDTO.remarks,
                    isEvaluated = true
                )
            is StudentMMCQResponseDTO ->
                response.copy(
                    marks = gradeDTO.marks,
                    remarks = gradeDTO.remarks,
                    isEvaluated = true
                )
            is StudentTrueFalseResponseDTO ->
                response.copy(
                    marks = gradeDTO.marks,
                    remarks = gradeDTO.remarks,
                    isEvaluated = true
                )
            is StudentDescriptiveResponseDTO ->
                response.copy(
                    marks = gradeDTO.marks,
                    remarks = gradeDTO.remarks,
                    isEvaluated = true
                )
            is StudentCodingResponseDTO ->
                response.copy(
                    marks = gradeDTO.marks,
                    remarks = gradeDTO.remarks,
                    isEvaluated = true
                )
            is StudentFillUpResponseDTO ->
                response.copy(
                    marks = gradeDTO.marks,
                    remarks = gradeDTO.remarks,
                    isEvaluated = true
                )
            is StudentMatchResponseDTO ->
                response.copy(
                    marks = gradeDTO.marks,
                    remarks = gradeDTO.remarks,
                    isEvaluated = true
                )
            is StudentFileUploadResponseDTO ->
                response.copy(
                    marks = gradeDTO.marks,
                    remarks = gradeDTO.remarks,
                    isEvaluated = true
                )
            else -> throw IllegalArgumentException("Unknown response type for question ${gradeDTO.questionId}")
        }
    }
}