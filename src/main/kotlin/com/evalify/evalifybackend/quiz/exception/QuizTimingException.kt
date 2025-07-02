package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException

/** Exception thrown when quiz timing constraints are violated */
class QuizTimingException(quizId: String, message: String) :
        BusinessLogicException("Quiz timing violation for quiz $quizId: $message")
