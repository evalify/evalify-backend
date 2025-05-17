package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.DTO.UpdateQuizCourseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.UpdateQuizLabDTO
import com.evalify.evalifybackend.quiz.domain.DTO.UpdateQuizStudentDTO
import com.evalify.evalifybackend.quiz.service.QuizCourseService
import com.evalify.evalifybackend.quiz.service.QuizLabService
import com.evalify.evalifybackend.quiz.service.QuizService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("quiz")
class QuizController(val quizService:QuizService,val quizCourseService: QuizCourseService,val quizLabService: QuizLabService) {
@PutMapping("{quizId}/add-student")
fun addStudent(@RequestBody studentDTO:UpdateQuizStudentDTO,@PathVariable quizId:UUID){
    quizService.addStudentToQuiz(studentId = studentDTO.studentId, quizId = quizId)
}
@PutMapping("{quizId}/remove-student")
fun removeStudent(@RequestBody studentDTO:UpdateQuizStudentDTO,@PathVariable quizId:UUID){
        quizService.removeStudentFromQuiz(studentId = studentDTO.studentId, quizId = quizId)
}
@PutMapping("{quizId}/add-course")
    fun addCourseToQuiz(@RequestBody courseDTO:UpdateQuizCourseDTO,@PathVariable quizId:UUID){
        quizCourseService.assignCourseToQuiz(courseId = courseDTO.course, quizId = quizId)
    }
    @PutMapping("{quizId}/remove-course")
    fun removeCourse(@RequestBody courseDTO:UpdateQuizCourseDTO,@PathVariable quizId:UUID){
        quizCourseService.removeCourseFromQuiz(courseId = courseDTO.course, quizId = quizId)
    }

    @PutMapping("{quizId}/add-lab")
    fun addLabToQuiz(@RequestBody labDTO:UpdateQuizLabDTO,@PathVariable quizId:UUID){
        quizLabService.assignLabToQuiz(labId = labDTO.lab, quizId = quizId)
    }
    @PutMapping("{quizId}/remove-lab")
    fun removeLabFromQuiz(@RequestBody labDTO:UpdateQuizLabDTO,@PathVariable quizId:UUID){
        quizLabService.removeLabToQuiz(labId = labDTO.lab, quizId = quizId)
    }


}