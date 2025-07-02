package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.ConflictException

/**
 * Exception thrown when attempting to share a bank with a user who already has access
 */
class BankAlreadySharedException(bankId: String, userId: String) : 
    ConflictException("Bank $bankId is already shared with user $userId")
