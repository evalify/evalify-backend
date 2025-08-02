package com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz

import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizTagDTO
import com.evalify.evalifybackend.quiz.domain.QuizTags

data class QuizQuestionReturnDTO(
    val quizTags : List<QuizTagsReturnDTO>,
    val questions : List<QuizQuestionsReturnDTO>,
    val message : String? = null
) {

}