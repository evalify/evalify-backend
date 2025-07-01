package com.evalify.evalifybackend.quiz.controller


import com.evalify.evalifybackend.quiz.domain.DTO.criteria.PermutationsDTO
import com.evalify.evalifybackend.quiz.domain.DTO.criteria.SelectionCriteriaDTO

import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.UpdateQuizCourseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.UpdateQuizLabDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.UpdateQuizStudentDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.CreateQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.PatchQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.ShareQuizDTO

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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import org.springframework.web.bind.annotation.RequestParam

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
    @PostMapping("{quizId}/add-student")
fun addStudent(@RequestBody studentDTO:UpdateQuizStudentDTO,@PathVariable quizId:UUID){
    quizService.addStudentToQuiz(studentId = studentDTO.studentId, quizId = quizId)
}
    @DeleteMapping("{quizId}/remove-student")
fun removeStudent(@RequestBody studentDTO:UpdateQuizStudentDTO,@PathVariable quizId:UUID){
        quizService.removeStudentFromQuiz(studentId = studentDTO.studentId, quizId = quizId)
}
    @PostMapping("{quizId}/add-course")
    fun addCourseToQuiz(@RequestBody courseDTO:UpdateQuizCourseDTO,@PathVariable quizId:UUID){
        quizCourseService.assignCourseToQuiz(courseId = courseDTO.course, quizId = quizId)
    }
    @DeleteMapping("{quizId}/remove-course")
    fun removeCourse(@RequestBody courseDTO:UpdateQuizCourseDTO,@PathVariable quizId:UUID){
        quizCourseService.removeCourseFromQuiz(courseId = courseDTO.course, quizId = quizId)
    }

    @PostMapping("{quizId}/add-lab")
    fun addLabToQuiz(@RequestBody labDTO:UpdateQuizLabDTO,@PathVariable quizId:UUID){
        quizLabService.assignLabToQuiz(labId = labDTO.lab, quizId = quizId)
    }
    @DeleteMapping("{quizId}/remove-lab")
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

    @PostMapping("{quizId}/share")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun shareQuiz(@PathVariable quizID : UUID, @RequestBody shareDTO: ShareQuizDTO){

        quizService.shareQuiz(quizID,shareDTO)
    }

    @PostMapping("{quizId}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun publishQuiz(@PathVariable quizId: UUID, @RequestParam(required = false) quizSets : Int,
         @RequestBody dto : SelectionCriteriaDTO){

        quizService.publishQuiz(quizId,quizSets,dto)
    }

    @GetMapping("{quizId}/combinations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun checkAvail(@PathVariable quizId: UUID, @RequestBody dto : SelectionCriteriaDTO) : ResponseEntity<PermutationsDTO>{
        val result = quizService.checkAvailability(quizId,dto)
        return ResponseEntity.ok(result)
    }




}