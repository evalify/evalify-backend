package com.evalify.evalifybackend.quiz.domain.DTO

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy

abstract class QuestionsReturnDTO
{
    val hint : String? = null
    val marks : Int = 0
    val bloomsTaxonomy : Taxonomy? = null
    val co : Int = 0
    val difficulty : Difficulty? = null

}