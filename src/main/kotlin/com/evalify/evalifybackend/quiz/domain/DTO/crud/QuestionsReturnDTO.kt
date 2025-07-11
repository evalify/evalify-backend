package com.evalify.evalifybackend.quiz.domain.DTO.crud

import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy
import java.util.UUID

open class QuestionsReturnDTO
    (
    open val hint : String? = null,
    open val questionId : UUID?,
    open val topics: List<ReturnTopicDTO>,

    open val marks : Int = 0,
    open val bloomsTaxonomy : Taxonomy? = null,
    open val co : Int = 0,
    open val difficulty : Difficulty? = null

)