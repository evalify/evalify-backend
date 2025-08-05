package com.evalify.evalifybackend.quiz.domain.DTO.crud

import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

open class QuestionsReturnDTO @JsonCreator constructor(
    @JsonProperty("hint") open val hint : String? = null,
    @JsonProperty("questionId") open val questionId : UUID?,
    @JsonProperty("topics") open val topics: List<ReturnTopicDTO>,
    @JsonProperty("marks") open val marks : Int = 0,
    @JsonProperty("bloomsTaxonomy") open val bloomsTaxonomy : Taxonomy? = null,
    @JsonProperty("co") open val co : Int = 0,
    @JsonProperty("difficulty") open val difficulty : Difficulty? = null
)