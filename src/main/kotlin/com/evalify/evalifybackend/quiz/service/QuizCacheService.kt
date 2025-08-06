package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizInfoDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizQuestionResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizStudentInfoDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.domain.QuizTags
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import jakarta.transaction.Transactional
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID


@Service
@Transactional
class QuizCacheService(
    private val quizStudentService: QuizStudentService,
    private val redisTemplate: RedisTemplate<String, QuizQuestionsReturnDTO>,
    private val responseMapRedisTemplate: RedisTemplate<String, ResponseDTO>,
    private val quizRepository: QuizRepository,
    private val quizStudentRepository: com.evalify.evalifybackend.quiz.repository.QuizStudentRepository,
) {
    fun getCachedQuizQuestions(quizId: UUID, studentId: String?): QuizQuestionReturnDTO? {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { NotFoundException("Quiz with id $quizId not found") }

        val key = "quiz:$quizId:student:$studentId:questions"
        val cachedQuestions = redisTemplate.opsForList().range(key, 0, -1)

        if (cachedQuestions.isNullOrEmpty()) {
            return null
        }

        val questionsList = cachedQuestions.filterNotNull()
        val tags = quizStudentService.getQuizTags(quizId)

        // Now responses is a Map<UUID, ResponseDTO>
        val responses: Map<UUID, ResponseDTO> = getAllAnswers(quizId, studentId)

        val questionResponse = questionsList.map { question ->
            QuizQuestionResponseDTO(
                question = question,
                response = responses[question.questions.questionId]  // ✅ Map lookup by UUID
            )
        }
        val existingStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString()) ?: throw NotFoundException("QuizStudent record not found")


        return QuizQuestionReturnDTO(
            questions = questionResponse,
            quizTags = tags.map { tag ->
                QuizTagsReturnDTO(
                    id = tag.id,
                    name = tag.name,
                    description = tag.description
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
            message = "Quiz has started successfully.",
            quizStudentInfo = QuizStudentInfoDTO(
                duration = existingStudent?.duration,
                endTime = existingStudent?.endTime,
                startTime = existingStudent?.startTime,
                violations = existingStudent?.violations,
                isViolated = existingStudent?.isViolated))

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
        val hashOps = responseMapRedisTemplate.opsForHash<UUID, ResponseDTO>()
        hashOps.put(key,answer.questionId,answer)

        //TODO() Set the ttl for this cache
    }


    fun getAllAnswers(
        quizId: UUID,
        studentId: String?
    ): Map<UUID, ResponseDTO> {
        val key = "quiz:$quizId:student:$studentId:answers"
        val hashOps = responseMapRedisTemplate.opsForHash<UUID, ResponseDTO>()
        return hashOps.entries(key)
    }

    /**
     * Clears all cached data for a specific quiz and student.
     * Useful for cleaning up after quiz submission or when data format changes.
     */
    fun clearStudentCache(quizId: UUID, studentId: String?) {
        val questionKey = "quiz:$quizId:student:$studentId:questions"
        val answerKey = "quiz:$quizId:student:$studentId:answers"
        
        redisTemplate.delete(questionKey)
        responseMapRedisTemplate.delete(answerKey)
    }

    /**
     * Clears all cache entries matching a pattern (use with caution).
     * Useful when changing data formats and need to clear all existing cache.
     */
    fun clearAllQuizCache(quizId: UUID) {
        val pattern = "quiz:$quizId:*"
        val keys = redisTemplate.keys(pattern)
        if (keys.isNotEmpty()) {
            redisTemplate.delete(keys)
        }
        
        val responseKeys = responseMapRedisTemplate.keys(pattern)
        if (responseKeys.isNotEmpty()) {
            responseMapRedisTemplate.delete(responseKeys)
        }
    }


}