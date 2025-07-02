package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException

/** Exception thrown when quiz business logic rules are violated */
class QuizBusinessLogicException(message: String) : BusinessLogicException(message)
