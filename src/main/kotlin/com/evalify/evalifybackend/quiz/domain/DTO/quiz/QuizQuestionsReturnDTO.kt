package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import java.util.UUID

data class QuizQuestionsReturnDTO(
    val question : BankQuestionsReturnDTO,
    val sectionId : UUID? = null
) {
}