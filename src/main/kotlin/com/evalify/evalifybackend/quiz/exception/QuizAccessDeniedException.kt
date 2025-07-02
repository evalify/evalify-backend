package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.UnauthorizedException

/** Exception thrown when user lacks access to a quiz */
class QuizAccessDeniedException(quizId: String, userId: String) :
        UnauthorizedException("User $userId does not have access to quiz $quizId")
