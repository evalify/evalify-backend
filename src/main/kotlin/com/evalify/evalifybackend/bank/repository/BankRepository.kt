package com.evalify.evalifybackend.bank.repository

import com.evalify.evalifybackend.bank.domain.Bank
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BankRepository : JpaRepository<Bank, UUID> {

    /** Find banks where user has access (either as owner or shared user) */
    @Query(
            """
        SELECT DISTINCT b FROM Bank b 
        JOIN b.sharedUsers su 
        WHERE su.user.id = :userId
    """
    )
    fun findBanksByUserAccess(@Param("userId") userId: String, pageable: Pageable): Page<Bank>

    /** Find banks created by a specific user */
    @Query("""
        SELECT b FROM Bank b 
        WHERE b.createdBy.id = :userId
    """)
    fun findBanksByCreatedBy(@Param("userId") userId: String, pageable: Pageable): Page<Bank>

    /** Search banks by name with user access filter */
    @Query(
            """
        SELECT DISTINCT b FROM Bank b 
        JOIN b.sharedUsers su 
        WHERE su.user.id = :userId 
        AND LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """
    )
    fun findBanksByUserAccessAndNameContaining(
            @Param("userId") userId: String,
            @Param("searchTerm") searchTerm: String,
            pageable: Pageable
    ): Page<Bank>
}
