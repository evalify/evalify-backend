package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO

class TrueFalseDTO(
val question:String,
val answers : Boolean,
val hintText: String?,
val markValue: Int,
val taxonomy: Taxonomy?,
val coValue: Int,
val difficultyLevel: Difficulty?
) : QuestionsReturnDTO(
hint = hintText,
marks = markValue,
bloomsTaxonomy = taxonomy,
co = coValue,
difficulty = difficultyLevel
)