package com.evalify.evalifybackend.bank.domain.DTO.bank

import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy
import java.util.UUID

open class BankQuestionsReturnDTO
    (
    open val hint : String? = null,
    open val marks : Int = 0,
    open val bloomsTaxonomy : Taxonomy? = null,
    open val co : Int = 0,
    open val difficulty : Difficulty? = null,
    open val topics : List<ReturnTopicDTO>? = emptyList(),
    open val questionId : UUID?,

)