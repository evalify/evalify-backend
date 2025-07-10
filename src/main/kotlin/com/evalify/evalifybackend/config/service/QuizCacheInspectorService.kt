package com.evalify.evalifybackend.config.service


import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class QuizCacheInspectorService(
    private val quizQuestionRedisTemplate: RedisTemplate<String, QuizQuestionsReturnDTO>,
    private val quizResponseRedisTemplate: RedisTemplate<String, ResponseDTO>
) {

    fun getAllStudentQuestionsCache(): Map<String, List<QuizQuestionsReturnDTO>> {
        val pattern = "quiz:*:student:*:questions"
        val keys = quizQuestionRedisTemplate.keys(pattern)
        val result = mutableMapOf<String, List<QuizQuestionsReturnDTO>>()
        keys?.forEach { key ->
            val list = quizQuestionRedisTemplate.opsForList().range(key, 0, -1) ?: emptyList()
            result[key] = list
        }
        return result
    }

    fun getAllStudentAnswersCache(): Map<String, List<ResponseDTO>> {
        val pattern = "quiz:*:student:*:answers"
        val keys = quizResponseRedisTemplate.keys(pattern)
        val result = mutableMapOf<String, List<ResponseDTO>>()
        keys?.forEach { key ->
            val values = quizResponseRedisTemplate.opsForHash<Any, Any>().values(key)
            @Suppress("UNCHECKED_CAST")
            result[key] = values.mapNotNull { it as? ResponseDTO }
        }
        return result
    }

}