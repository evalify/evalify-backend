package com.evalify.evalifybackend.quiz.mapper

import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizUpdateDTO
import com.evalify.evalifybackend.quiz.domain.Quiz

fun Quiz.updateQuiz(quizUpdateDTO: QuizUpdateDTO):Quiz{
    return Quiz(
        id = this.id,
        name = quizUpdateDTO.name ?: this.name,
        description = quizUpdateDTO.description ?: this.description,
        instructions = quizUpdateDTO.instructions ?: this.instructions,
        startTime = quizUpdateDTO.startTime ?: this.startTime,
        endTime = quizUpdateDTO.endTime ?: this.endTime,
        duration = quizUpdateDTO.duration ?: this.duration,
        password = quizUpdateDTO.password ?: this.password,

        fullScreen = quizUpdateDTO.fullScreen ?: this.fullScreen,
        shuffleQuestions = quizUpdateDTO.shuffleQuestions ?: this.shuffleQuestions,
        shuffleOptions = quizUpdateDTO.shuffleOptions ?: this.shuffleOptions,
        linearQuiz = quizUpdateDTO.linearQuiz ?: this.linearQuiz,
        calculator = quizUpdateDTO.calculator ?: this.calculator,
        autoSubmit = quizUpdateDTO.autoSubmit ?: this.autoSubmit,
        publishResult = quizUpdateDTO.publishResult ?: this.publishResult,
        publishQuiz = quizUpdateDTO.publishQuiz ?: this.publishQuiz,

        // Preserve relationships and metadata
        section = this.section,
        course = this.course,
        student = this.student,
        lab = this.lab,
        batch = this.batch,
        createdAt = this.createdAt,
//        createdBy = this.createdBy
    )
}
