package com.evalify.evalifybackend.section.domain.DTO

import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import java.util.UUID

data class MoveSectionQuestionDTO(
    val toSectionId : UUID,
    val questions : List<QuizQuestion>
) {
}