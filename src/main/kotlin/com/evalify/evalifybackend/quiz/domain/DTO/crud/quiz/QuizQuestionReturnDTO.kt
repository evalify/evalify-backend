package com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz

import com.evalify.evalifybackend.quiz.domain.QuizTags

data class QuizQuestionReturnDTO(
    val quizTags : List<QuizTags>,
    val questions : List<QuizQuestionsReturnDTO>,
    val message : String? = null
) {

}