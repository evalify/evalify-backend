package com.evalify.evalifybackend.bank.domain.DTO.topic

import com.evalify.evalifybackend.quiz.question.domain.Topic

data class SearchTopicDTO(
    val topics : List<Topic>
) {
}