package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.UnauthorizedException

/**
 * Exception thrown when user lacks access to a bank
 */
class BankAccessDeniedException(bankId: String, userId: String) : 
    UnauthorizedException("User $userId does not have access to bank $bankId")
