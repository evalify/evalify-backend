package com.evalify.evalifybackend.questions.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException

/** Exception thrown when question state is invalid for requested operation */
class QuestionStateException(questionId: String, operation: String, state: String) :
        BusinessLogicException(
                "Cannot perform '$operation' on question $questionId in state: $state"
        )
