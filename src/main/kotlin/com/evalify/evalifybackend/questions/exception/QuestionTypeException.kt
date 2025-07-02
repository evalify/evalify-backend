package com.evalify.evalifybackend.questions.exception

import com.evalify.evalifybackend.core.exception.ConflictException

/** Exception thrown when question type operations are invalid */
class QuestionTypeException(questionId: String, operation: String, currentType: String) :
        ConflictException(
                "Cannot perform '$operation' on question $questionId of type $currentType"
        )
