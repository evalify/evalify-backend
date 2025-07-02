package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException

/** Exception thrown when quiz criteria requirements are not met */
class QuizCriteriaException(message: String, criteriaType: String? = null) :
        BusinessLogicException(
                "Quiz criteria error${criteriaType?.let { " [$it]" } ?: ""}: $message"
        )
