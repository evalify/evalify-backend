package com.evalify.evalifybackend.bank.repository

import com.evalify.evalifybackend.bank.domain.BankQuestion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID


@RepositoryRestResource(path = "bank-question")
interface BankQuestionRepo : JpaRepository<BankQuestion, UUID> {
}