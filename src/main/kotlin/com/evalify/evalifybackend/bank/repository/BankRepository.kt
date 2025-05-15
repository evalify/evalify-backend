package com.evalify.evalifybackend.bank.repository

import com.evalify.evalifybackend.bank.domain.Bank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "bank")
interface BankRepository : JpaRepository<Bank, UUID> {
}