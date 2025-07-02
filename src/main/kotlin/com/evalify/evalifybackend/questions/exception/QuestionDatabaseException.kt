package com.evalify.evalifybackend.questions.exception

import com.evalify.evalifybackend.core.exception.ExternalServiceException

/** Exception thrown when question database operations fail */
class QuestionDatabaseException(
        operation: String,
        questionId: String? = null,
        cause: Throwable? = null
) :
        ExternalServiceException(
                "Question database operation '$operation' failed${questionId?.let { " for question $it" } ?: ""}",
                "question-database",
                cause
        )
