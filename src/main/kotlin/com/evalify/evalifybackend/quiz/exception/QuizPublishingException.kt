package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException

/** Exception thrown when quiz publishing fails due to business logic constraints */
class QuizPublishingException(message: String) : BusinessLogicException(message)
