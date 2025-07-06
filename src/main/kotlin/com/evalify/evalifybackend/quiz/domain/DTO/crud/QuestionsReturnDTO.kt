package com.evalify.evalifybackend.quiz.domain.DTO.crud

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy

open class QuestionsReturnDTO
    (
    open val hint : String? = null,
    open val marks : Int = 0,
    open val bloomsTaxonomy : Taxonomy? = null,
    open val co : Int = 0,
    open val difficulty : Difficulty? = null

)