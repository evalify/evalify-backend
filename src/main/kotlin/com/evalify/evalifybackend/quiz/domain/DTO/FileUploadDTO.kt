package com.evalify.evalifybackend.quiz.domain.DTO

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy

data class FileUploadDTO (val question: String,
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
){

}