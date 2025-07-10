package com.evalify.evalifybackend.section.exception

class QuizNotFoundException(quizId: String) : RuntimeException("Quiz with id '$quizId' not found")
