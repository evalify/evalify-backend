package com.evalify.evalifybackend.quiz.question.repository

import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BankQuestionRepository : JpaRepository<BankQuestion, UUID> {
}