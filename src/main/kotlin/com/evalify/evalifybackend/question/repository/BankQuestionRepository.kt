package com.evalify.evalifybackend.question.repository

import com.evalify.evalifybackend.question.domain.bankQuestion.BankQuestion
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BankQuestionRepository : JpaRepository<BankQuestion, UUID> {
}