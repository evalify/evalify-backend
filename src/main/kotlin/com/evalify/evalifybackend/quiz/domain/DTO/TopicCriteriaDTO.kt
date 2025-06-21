package com.evalify.evalifybackend.quiz.domain.DTO

import java.util.UUID

data class TopicCriteriaDTO(
    val topicId: UUID,
    val easy : Int,
    val medium : Int,
    val hard : Int
) {
}