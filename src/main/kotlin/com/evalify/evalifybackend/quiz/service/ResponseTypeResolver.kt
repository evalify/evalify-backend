package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.responses.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ResponseTypeResolver(
    private val objectMapper: ObjectMapper
) {
    
    fun mapToResponseDTO(body: Map<String, Any>, questionType: QuestionTypes): ResponseDTO {
        val questionId = UUID.fromString(body["questionId"] as String)
        val duration = (body["duration"] as? Number)?.toLong() ?: 0L
        
        return when (questionType) {
            QuestionTypes.MCQ -> {
                val answer = body["answer"]?.let { UUID.fromString(it as String) }
                MCQResponseDTO(
                    questionId = questionId,
                    answer = answer,
                    duration = duration
                )
            }
            
            QuestionTypes.MMCQ -> {
                val answer = (body["answer"] as? List<*>)?.mapNotNull { 
                    it?.let { UUID.fromString(it as String) } 
                }
                MMCQResponseDTO(
                    questionId = questionId,
                    answer = answer,
                    duration = duration
                )
            }
            
            QuestionTypes.TRUEFALSE -> {
                val answer = body["answer"] as? Boolean
                TrueFalseResponseDTO(
                    questionId = questionId,
                    answer = answer,
                    duration = duration
                )
            }
            
            QuestionTypes.FILL_UP -> {
                val answer = (body["answer"] as? List<*>)?.map { item ->
                    val map = item as Map<*, *>
                    BlankResponseDTO(
                        id = map["id"] as String,
                        answer = map["answer"] as? String
                    )
                }
                FillUpResponseDTO(
                    questionId = questionId,
                    answer = answer,
                    duration = duration
                )
            }
            
            QuestionTypes.MATCH_THE_FOLLOWING -> {
                val answer = (body["answer"] as? List<*>)?.map { item ->
                    val map = item as Map<*, *>
                    MatchPairResponse(
                        leftPairId = UUID.fromString(map["leftPairId"] as String),
                        rightPairId = UUID.fromString(map["rightPairId"] as String)
                    )
                }
                MatchResponseDTO(
                    questionId = questionId,
                    answer = answer,
                    duration = duration
                )
            }
            
            QuestionTypes.DESCRIPTIVE -> {
                val answer = body["answer"] as? String
                DescriptiveResponseDTO(
                    questionId = questionId,
                    answer = answer,
                    duration = duration
                )
            }
            
            QuestionTypes.FILE_UPLOAD -> {
                val answer = body["answer"] as? String
                FileUploadResponseDTO(
                    questionId = questionId,
                    answer = answer,
                    duration = duration
                )
            }
            
            QuestionTypes.CODING -> {
                val answer = body["answer"] as? String
                CodingResponseDTO(
                    questionId = questionId,
                    answer = answer,
                    duration = duration
                )
            }
        }
    }
    
    fun deserializeResponseWithType(body: Map<String, Any>, questionType: QuestionTypes): ResponseDTO {
        // Add the type information to the map for Jackson polymorphic deserialization
        val bodyWithType = body.toMutableMap()
        bodyWithType["type"] = questionType.name
        
        return objectMapper.convertValue(bodyWithType, ResponseDTO::class.java)
    }
}
