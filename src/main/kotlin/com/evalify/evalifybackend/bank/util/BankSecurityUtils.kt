package com.evalify.evalifybackend.bank.util

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.exception.BankAccessDeniedException
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedTags
import java.util.UUID

/**
 * Utility class for bank security operations
 */
object BankSecurityUtils {

    /**
     * Checks if a user has access to a bank
     */
    fun checkBankAccess(bank: Bank, userId: String, requiredAccess: SharedTags = SharedTags.SHARED): Boolean {
        return bank.sharedUsers.any { bankUser ->
            bankUser.user?.id == userId && hasRequiredAccess(bankUser.tags, requiredAccess)
        }
    }

    /**
     * Ensures user has access to a bank, throws exception if not
     */
    fun ensureBankAccess(bank: Bank, userId: String, requiredAccess: SharedTags = SharedTags.SHARED) {
        if (!checkBankAccess(bank, userId, requiredAccess)) {
            throw BankAccessDeniedException(bank.id.toString(), userId)
        }
    }

    /**
     * Checks if user is owner of the bank
     */
    fun isOwner(bank: Bank, userId: String): Boolean {
        return checkBankAccess(bank, userId, SharedTags.OWNER)
    }

    /**
     * Ensures user is owner of the bank
     */
    fun ensureOwnership(bank: Bank, userId: String) {
        ensureBankAccess(bank, userId, SharedTags.OWNER)
    }

    /**
     * Gets user's access level for a bank
     */
    fun getUserAccessLevel(bank: Bank, userId: String): SharedTags? {
        return bank.sharedUsers.find { it.user?.id == userId }?.tags
    }

    private fun hasRequiredAccess(userAccess: SharedTags, requiredAccess: SharedTags): Boolean {
        return when (requiredAccess) {
            SharedTags.OWNER -> userAccess == SharedTags.OWNER
            SharedTags.SHARED -> userAccess == SharedTags.OWNER || userAccess == SharedTags.SHARED
        }
    }
}
