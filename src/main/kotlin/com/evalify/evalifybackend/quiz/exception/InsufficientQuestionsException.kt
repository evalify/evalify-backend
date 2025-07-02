package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException

/** Exception thrown when insufficient questions are available for quiz generation */
class InsufficientQuestionsException(requiredCount: Int, availableCount: Int, criteria: String) :
        BusinessLogicException(
                "Insufficient questions for $criteria: required $requiredCount, available $availableCount"
        )
