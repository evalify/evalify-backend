package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException

/** Exception thrown when attempting to modify immutable bank question properties */
class BankQuestionImmutableException(questionId: String, property: String) :
        BusinessLogicException(
                "Cannot modify immutable property '$property' of bank question $questionId"
        )
