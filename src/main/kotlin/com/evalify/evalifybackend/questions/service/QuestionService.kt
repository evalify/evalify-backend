package com.evalify.evalifybackend.questions.service

import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.PairDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.Pair
import org.springframework.stereotype.Service

@Service
class QuestionService {
    fun createPair(text: String): PairDTO {
        val pair = Pair(text = text)
        return PairDTO(id = pair.id , text = pair.text)
    }
}