package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.domain.QuizTags
import jakarta.transaction.Transactional
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID


@Service
@Transactional
class QuizCacheService(
    private val quizStudentService: QuizStudentService,
    private val redisTemplate: RedisTemplate<String, QuizQuestionsReturnDTO>
) {
    fun getCachedQuizQuestions(quizId: UUID, studentId: String?, requestTime: Instant): QuizQuestionReturnDTO? {
        val key = "quiz:$quizId:student:$studentId:questions"
        val cachedQuestions = redisTemplate.opsForList().range(key, 0, -1)
        
        if (cachedQuestions.isNullOrEmpty()) {
            return null
        }
        
        val questionsList = cachedQuestions.filterNotNull()
        val tags = quizStudentService.getQuizTags(quizId)
        
        return QuizQuestionReturnDTO(
            questions = questionsList,
            quizTags = tags.map { tag ->
                QuizTagsReturnDTO(
                    id = tag.id,
                    name = tag.name,
                    description = tag.description
                )
            }
        )
    }
    fun storeStudentQuestions(quizId: UUID,studentId: String?,questions: List<QuizQuestionsReturnDTO>){
        val qId = quizId.toString()
        val key = "quiz:$qId:student:$studentId:questions"
        val listOps = redisTemplate.opsForList()

        redisTemplate.delete(key)
        questions.forEach {question ->
            listOps.rightPush(key,question)
        }
        //TODO() Set the ttl for this cache

    }

    fun updateCache(quizId: UUID,studentId: String?,answer: ResponseDTO){
        val qId = quizId.toString()
        val key = "quiz:$qId:student:$studentId:answers"
        redisTemplate.opsForHash<UUID, ResponseDTO>().put(key, answer.questionId,answer)
        //TODO() Set the ttl for this cache
    }

    fun getAllAnswers(quizId: UUID, studentId: String?): List<ResponseDTO> {
        val qId = quizId.toString()
        val key = "quiz:$qId:student:$studentId:answers"
        val hashOps = redisTemplate.opsForHash<UUID, ResponseDTO>()
        return hashOps.values(key).toList()
    }


}