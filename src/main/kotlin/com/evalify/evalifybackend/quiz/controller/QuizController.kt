package com.evalify.evalifybackend.quiz.controller


import com.evalify.evalifybackend.quiz.domain.DTO.AddBankQuestionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.PermutationsDTO
import com.evalify.evalifybackend.quiz.domain.DTO.SelectionCriteriaDTO
import com.evalify.evalifybackend.quiz.domain.DTO.TopicCriteriaDTO

import com.evalify.evalifybackend.quiz.domain.DTO.UpdateQuizCourseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.UpdateQuizLabDTO
import com.evalify.evalifybackend.quiz.domain.DTO.UpdateQuizStudentDTO
import com.evalify.evalifybackend.quiz.domain.DTO.CreateQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.PatchQuizDTO

import com.evalify.evalifybackend.quiz.service.QuizCourseService
import com.evalify.evalifybackend.quiz.service.QuizLabService
import com.evalify.evalifybackend.quiz.service.QuizQuestionService
import com.evalify.evalifybackend.quiz.service.QuizService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/quiz")
open class QuizController(val quizService:QuizService,val quizCourseService: QuizCourseService,val quizLabService: QuizLabService,val quizQuestionService: QuizQuestionService) {

    @PostMapping("/")
    fun createQuiz(@RequestBody quizDTO: CreateQuizDTO)
    {
        val userId: String? = SecurityUtils.getCurrentUserId()
        quizService.createQuiz(quizDTO,userId)
    }


    @PatchMapping("/{quizId}")
    fun editQuiz(@RequestBody dto : PatchQuizDTO, @PathVariable quizId: UUID)
    {
        quizService.editQuiz(dto,quizId)
    }
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

    /**
     * Deletes a quiz by its ID
     * @param quizId The UUID of the quiz to delete
     */
    @DeleteMapping("{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteQuiz(@PathVariable quizId: UUID) {
        quizService.deleteQuiz(quizId)
    }




}