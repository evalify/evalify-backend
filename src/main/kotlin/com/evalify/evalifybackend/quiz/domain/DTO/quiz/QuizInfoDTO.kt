package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import ch.qos.logback.classic.spi.PackagingDataCalculator
import java.util.UUID
import javax.swing.text.html.FormSubmitEvent

data class QuizInfoDTO(
    val quizId : UUID?,
    val quizName : String?,
    val calculator: Boolean?,
    val kioskMode: Boolean?,
    val fullScreen: Boolean?,
    val autoSubmit: Boolean?,
    val linearQuiz: Boolean?


    ) {
}