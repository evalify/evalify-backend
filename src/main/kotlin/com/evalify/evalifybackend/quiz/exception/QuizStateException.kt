package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException

/** Exception thrown when quiz operations are performed in invalid state */
class QuizStateException(quizId: String, operation: String, currentState: String) :
        BusinessLogicException(
                "Cannot perform '$operation' on quiz $quizId - current state: $currentState"
        )
