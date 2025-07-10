package com.evalify.evalifybackend.section.exception

class QuestionMoveException(fromSectionId: String, toSectionId: String, cause: Throwable? = null) : RuntimeException("Failed to move questions from section '$fromSectionId' to section '$toSectionId'", cause)
