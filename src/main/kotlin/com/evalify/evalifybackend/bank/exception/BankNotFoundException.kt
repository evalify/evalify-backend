package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.NotFoundException

/**
 * Exception thrown when a bank is not found
 */
class BankNotFoundException(bankId: String) : NotFoundException("Bank with ID $bankId not found")
