package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.ExternalServiceException

/** Exception thrown when database operations fail in quiz context */
class QuizDatabaseException(operation: String, cause: Throwable? = null) :
        ExternalServiceException(
                "Quiz database operation '$operation' failed",
                "quiz-database",
                cause
        )
