package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy

open class BankQuestionsReturnDTO
    (
    val hint : String? = null,
    val marks : Int = 0,
    val bloomsTaxonomy : Taxonomy? = null,
    val co : Int = 0,
    val difficulty : Difficulty? = null

)