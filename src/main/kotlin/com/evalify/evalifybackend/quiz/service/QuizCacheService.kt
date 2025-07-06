package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class QuizCacheService(
    private val quizStudentService: QuizStudentService,
    private val redisTemplate: RedisTemplate<String, QuizQuestionsReturnDTO>
) {
    fun storeStudentQuestions(quizId: UUID,studentId: String,questions: List<QuizQuestionsReturnDTO>){
        val qId = quizId.toString()
        val key = "quiz:$qId:student:$studentId:questions"
        val listOps = redisTemplate.opsForList()

        redisTemplate.delete(key)
        questions.forEach {question ->
            listOps.rightPush(key,question)
        }
        //TODO() Set the ttl for this cache

    }
}