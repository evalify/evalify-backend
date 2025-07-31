package com.evalify.evalifybackend.quiz.domain.DTO.manager

enum class StudentStatus{
    ACTIVE,
    INACTIVE,
    MISSED,
    COMPLETED
}

data class QuizStudentsDTO (
    val id:String?,
    val name:String,
    val email:String,
    val phoneNumber:String,
    val status: StudentStatus,
){
}