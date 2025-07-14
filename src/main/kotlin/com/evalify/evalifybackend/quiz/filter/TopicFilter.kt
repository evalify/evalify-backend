package com.evalify.evalifybackend.quiz.filter

import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import java.util.UUID

class TopicFilter(
    val topics:List<Topic>?
): QuestionFilter {

  override fun filter(questions:List<BankQuestion>): List<BankQuestion> {
        return if(topics != null) questions.filter { bankQuestion ->  bankQuestion.question.topic.any { it in topics } } else questions
    }
}