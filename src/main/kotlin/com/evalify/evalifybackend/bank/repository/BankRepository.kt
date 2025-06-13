package com.evalify.evalifybackend.bank.repository

import com.evalify.evalifybackend.bank.domain.Bank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

interface BankRepository : JpaRepository<Bank, UUID> {

    @Query("SELECT b FROM Bank b LEFT JOIN FETCH b.bankQuestion WHERE b.id = :id")
    fun findByIdWithQuestions(@Param("id") id: UUID): Bank

}