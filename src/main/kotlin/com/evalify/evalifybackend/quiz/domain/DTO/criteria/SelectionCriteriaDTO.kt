package com.evalify.evalifybackend.quiz.domain.DTO.criteria

data class SelectionCriteriaDTO(
   val noSets : Int = 1,
   val quizSets : Int,
   val criteria: List<TopicCriteriaDTO>,
   val totalMarks: Int
)