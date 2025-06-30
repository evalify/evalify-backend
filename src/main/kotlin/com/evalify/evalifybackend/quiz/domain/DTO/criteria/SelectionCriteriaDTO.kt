package com.evalify.evalifybackend.quiz.domain.DTO.criteria

data class SelectionCriteriaDTO(
   val criteria: List<TopicCriteriaDTO>,
   val totalMarks: Int
)