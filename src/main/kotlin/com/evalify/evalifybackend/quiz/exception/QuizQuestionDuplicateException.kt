package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.ConflictException

/** Exception thrown when attempting to duplicate quiz questions */
class QuizQuestionDuplicateException(questionId: String, sectionId: String) :
        ConflictException("Question $questionId already exists in section $sectionId")
