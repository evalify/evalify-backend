package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.quiz.question.domain.Topic

data class SearchTopicDTO(
    val topics : List<Topic>
) {
}