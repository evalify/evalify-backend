package com.evalify.evalifybackend.quiz.domain.DTO

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import java.util.UUID

data class AddQuestionsToQuizDTO(
    val topicId:List<UUID>?,
    val bankId:List<UUID>?,
    val difficulty:List<Difficulty>?,
    val noOfQuestions:Int,
    val questionType:List<QuestionTypes>?
) {
}