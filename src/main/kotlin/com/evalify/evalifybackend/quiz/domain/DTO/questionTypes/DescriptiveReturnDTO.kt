package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import java.util.UUID

data class DescriptiveReturnDTO @JsonCreator constructor(
        @JsonProperty("question") val question: String,
        @JsonProperty("topics") override val topics: List<ReturnTopicDTO>,
        @JsonProperty("questionId") override val questionId: UUID?,
        @JsonProperty("hint") override val hint: String?,
        @JsonProperty("marks") override val marks: Int,
        @JsonProperty("bloomsTaxonomy") override val bloomsTaxonomy: Taxonomy?,
        @JsonProperty("co") override val co: Int,
        @JsonProperty("difficulty") override val difficulty: Difficulty?
) :
        QuestionsReturnDTO(
                hint = hint,
                marks = marks,
                bloomsTaxonomy = bloomsTaxonomy,
                co = co,
                difficulty = difficulty,
                topics = topics,
                questionId = questionId
        ) {}
