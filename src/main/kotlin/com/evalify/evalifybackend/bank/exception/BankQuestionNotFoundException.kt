package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.NotFoundException

/**
 * Exception thrown when a bank question is not found
 */
class BankQuestionNotFoundException(questionId: String) : NotFoundException("Bank question with ID $questionId not found")
