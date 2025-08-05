package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.FunctionParamDTO
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import java.util.UUID

class CodingReturnDTO @JsonCreator constructor(
        @JsonProperty("question") val question: String,
        @JsonProperty("questionId") override val questionId: UUID?,
        @JsonProperty("driverCode") val driverCode: String? = null,
        @JsonProperty("language") val language: List<String>? = emptyList(),
        @JsonProperty("testcases") val testcases: List<TestCaseDTO>,
        @JsonProperty("hint") override val hint: String?,
        @JsonProperty("marks") override val marks: Int,
        @JsonProperty("bloomsTaxonomy") override val bloomsTaxonomy: Taxonomy?,
        @JsonProperty("co") override val co: Int,
        @JsonProperty("difficulty") override val difficulty: Difficulty?,
        @JsonProperty("topics") override val topics: List<ReturnTopicDTO>
) :
        QuestionsReturnDTO(
                hint = hint,
                marks = marks,
                bloomsTaxonomy = bloomsTaxonomy,
                co = co,
                difficulty = difficulty,
                topics = topics,
                questionId = questionId
        )
