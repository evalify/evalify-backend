package com.evalify.evalifybackend.quiz.domain.DTO

import java.util.UUID

data class QuizTagsReturnDTO(
    val id : UUID?,
    val name : String?,
    val description : String? = null,

) {
}