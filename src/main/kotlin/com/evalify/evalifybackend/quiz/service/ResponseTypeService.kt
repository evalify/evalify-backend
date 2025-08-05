package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.quiz.domain.DTO.responses.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ResponseTypeService(
    private val questionRepository: QuestionRepository,
    private val objectMapper: ObjectMapper
) {
    
    /**
     * Creates the appropriate ResponseDTO subclass based on the question type.
     * 
     * This method:
     * 1. Looks up the question by questionId
     * 2. Determines the question type using getQuestionType()
     * 3. Creates the appropriate ResponseDTO subclass
     * 4. Maps the generic answer data to the specific type
     * 
     * @param questionId The UUID of the question
     * @param duration Time spent on the question in milliseconds
     * @param answer The raw answer data (can be UUID, String, List, etc.)
     * @return Appropriate ResponseDTO subclass
     * @throws NotFoundException if question not found
     * @throws IllegalArgumentException if answer format is invalid for question type
     */
    fun createTypedResponse(questionId: UUID, duration: Long, answer: Any?): ResponseDTO {
        // Find the question to determine its type
        val question = questionRepository.findById(questionId)
            .orElseThrow { NotFoundException("Question with id $questionId not found") }
        
        val questionType = question.getQuestionType()
        
        return when (questionType) {
            QuestionTypes.MCQ -> {
                val answerUuid = parseUUID(answer, "MCQ answer must be a valid UUID")
                MCQResponseDTO(
                    questionId = questionId,
                    answer = answerUuid,
                    duration = duration
                )
            }
            
            QuestionTypes.MMCQ -> {
                val answerList = parseUUIDList(answer, "MMCQ answer must be a list of UUIDs")
                MMCQResponseDTO(
                    questionId = questionId,
                    answer = answerList,
                    duration = duration
                )
            }
            
            QuestionTypes.TRUEFALSE -> {
                val answerBoolean = parseBoolean(answer, "True/False answer must be a boolean")
                TrueFalseResponseDTO(
                    questionId = questionId,
                    answer = answerBoolean,
                    duration = duration
                )
            }
            
            QuestionTypes.DESCRIPTIVE -> {
                val answerString = parseString(answer, "Descriptive answer must be a string")
                DescriptiveResponseDTO(
                    questionId = questionId,
                    answer = answerString,
                    duration = duration
                )
            }
            
            QuestionTypes.CODING -> {
                val answerString = parseString(answer, "Coding answer must be a string")
                CodingResponseDTO(
                    questionId = questionId,
                    answer = answerString,
                    duration = duration
                )
            }
            
            QuestionTypes.FILE_UPLOAD -> {
                val answerString = parseString(answer, "File upload answer must be a string path")
                FileUploadResponseDTO(
                    questionId = questionId,
                    answer = answerString,
                    duration = duration
                )
            }
            
            QuestionTypes.FILL_UP -> {
                val answerList = parseBlankResponseList(answer, "Fill up answer must be a list of blank responses")
                FillUpResponseDTO(
                    questionId = questionId,
                    answer = answerList,
                    duration = duration
                )
            }
            
            QuestionTypes.MATCH_THE_FOLLOWING -> {
                val answerList = parseMatchPairList(answer, "Match answer must be a list of match pairs")
                MatchResponseDTO(
                    questionId = questionId,
                    answer = answerList,
                    duration = duration
                )
            }
        }
    }
    
    private fun parseUUID(answer: Any?, errorMessage: String): UUID? {
        return when (answer) {
            null -> null
            is String -> try { UUID.fromString(answer) } catch (e: Exception) { 
                throw IllegalArgumentException("$errorMessage. Invalid UUID format: $answer") 
            }
            else -> throw IllegalArgumentException("$errorMessage. Expected string, got ${answer::class.simpleName}")
        }
    }
    
    private fun parseUUIDList(answer: Any?, errorMessage: String): List<UUID>? {
        return when (answer) {
            null -> null
            is List<*> -> {
                try {
                    answer.map { 
                        when (it) {
                            is String -> UUID.fromString(it)
                            else -> throw IllegalArgumentException("List must contain only UUID strings")
                        }
                    }
                } catch (e: Exception) {
                    throw IllegalArgumentException("$errorMessage. ${e.message}")
                }
            }
            else -> throw IllegalArgumentException("$errorMessage. Expected list, got ${answer::class.simpleName}")
        }
    }
    
    private fun parseBoolean(answer: Any?, errorMessage: String): Boolean? {
        return when (answer) {
            null -> null
            is Boolean -> answer
            is String -> answer.toBooleanStrictOrNull() 
                ?: throw IllegalArgumentException("$errorMessage. Invalid boolean string: $answer")
            else -> throw IllegalArgumentException("$errorMessage. Expected boolean, got ${answer::class.simpleName}")
        }
    }
    
    private fun parseString(answer: Any?, errorMessage: String): String? {
        return when (answer) {
            null -> null
            is String -> answer
            else -> throw IllegalArgumentException("$errorMessage. Expected string, got ${answer::class.simpleName}")
        }
    }
    
    private fun parseBlankResponseList(answer: Any?, errorMessage: String): List<BlankResponseDTO>? {
        return when (answer) {
            null -> null
            is List<*> -> {
                try {
                    answer.map { item ->
                        when (item) {
                            is Map<*, *> -> {
                                // Convert map to BlankResponseDTO
                                objectMapper.convertValue(item, BlankResponseDTO::class.java)
                            }
                            else -> throw IllegalArgumentException("List items must be objects with blank response structure")
                        }
                    }
                } catch (e: Exception) {
                    throw IllegalArgumentException("$errorMessage. ${e.message}")
                }
            }
            else -> throw IllegalArgumentException("$errorMessage. Expected list, got ${answer::class.simpleName}")
        }
    }
    
    private fun parseMatchPairList(answer: Any?, errorMessage: String): List<MatchPairResponse>? {
        return when (answer) {
            null -> null
            is List<*> -> {
                try {
                    answer.map { item ->
                        when (item) {
                            is Map<*, *> -> {
                                // Convert map to MatchPairResponse
                                objectMapper.convertValue(item, MatchPairResponse::class.java)
                            }
                            else -> throw IllegalArgumentException("List items must be objects with match pair structure")
                        }
                    }
                } catch (e: Exception) {
                    throw IllegalArgumentException("$errorMessage. ${e.message}")
                }
            }
            else -> throw IllegalArgumentException("$errorMessage. Expected list, got ${answer::class.simpleName}")
        }
    }
}
