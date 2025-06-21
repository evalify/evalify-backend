package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.lab.repository.LabRepository
import com.evalify.evalifybackend.quiz.domain.DTO.SelectionCriteriaDTO
import com.evalify.evalifybackend.quiz.domain.DTO.TopicCriteriaDTO

import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.DTO.CreateQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.PatchQuizDTO
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.topic.repository.TopicRepo
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import kotlin.String
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Service
class QuizService(private val quizRepository: QuizRepository,private val userRepository: UserRepository, private val courseRepository: CourseRepository, private val batchRepository: BatchRepository,
    private val labRepository: LabRepository, private val topicRepo: TopicRepo) {


    fun createQuiz(quizDTO: CreateQuizDTO, userId : String?)

    {

        val duration = quizDTO.durationInMinutes.toDuration(DurationUnit.MINUTES)
        val courses = courseRepository.findAllById(quizDTO.courseIds)
        val batches = batchRepository.findAllById(quizDTO.batchIds)
        val labs = labRepository.findAllById(quizDTO.labIds)
        if(userId == null) throw RuntimeException("User id cannot be null")
        val user = userRepository.findById(userId).orElseThrow{NotFoundException("User with id $userId not found")}

        val students = if(quizDTO.studentIds.isNotEmpty()){
            userRepository.findAllById(quizDTO.studentIds)
        }
        else{
            batches.flatMap { batch -> batch.students }
        }

        val quiz = Quiz(
            name = quizDTO.name,
            description = quizDTO.description,
            instructions = quizDTO.instructions,
            startTime = quizDTO.startTime,
            endTime = quizDTO.endTime,
            duration = duration,
            fullScreen = quizDTO.fullScreen,
            shuffleQuestions = quizDTO.shuffleQuestions,
            shuffleOptions = quizDTO.shuffleOptions,
            linearQuiz = quizDTO.linearQuiz,
            calculator = quizDTO.calculator,
            autoSubmit = quizDTO.autoSubmit,
            course = courses,
            batch = batches,
            student = students.toMutableList(),
            lab = labs,
            createdBy = user
           )

        quizRepository.save(quiz)
    }

    fun patchQuiz(
        existing: Quiz,
        dto: PatchQuizDTO,
        courseRepository: CourseRepository,
        batchRepository: BatchRepository,
        labRepository: LabRepository,
        userRepository: UserRepository
    ): Quiz {
        val updatedCourses = dto.courseIds?.let {
            courseRepository.findAllById(it).toMutableList()
        } ?: existing.course

        val updatedBatches = dto.batchIds?.let {
            batchRepository.findAllById(it).toMutableList()
        } ?: existing.batch

        val updatedLabs = dto.labIds?.let {
            labRepository.findAllById(it).toMutableList()
        } ?: existing.lab

        val updatedStudents = dto.studentIds?.let {
            userRepository.findAllById(it).toMutableList()
        } ?: existing.student

        val updatedQuiz = Quiz(
            id = existing.id,
            name = dto.name ?: existing.name,
            description = dto.description ?: existing.description,
            instructions = dto.instructions ?: existing.instructions,
            startTime = dto.startTime ?: existing.startTime,
            endTime = dto.endTime ?: existing.endTime,
            duration = dto.durationInMinutes?.toDuration(DurationUnit.MINUTES) ?: existing.duration,

            fullScreen = dto.fullScreen ?: existing.fullScreen,
            shuffleQuestions = dto.shuffleQuestions ?: existing.shuffleQuestions,
            shuffleOptions = dto.shuffleOptions ?: existing.shuffleOptions,
            linearQuiz = dto.linearQuiz ?: existing.linearQuiz,
            calculator = dto.calculator ?: existing.calculator,
            autoSubmit = dto.autoSubmit ?: existing.autoSubmit,
            publishResult = dto.publishResult ?: existing.publishResult,
            publishQuiz = dto.publishQuiz ?: existing.publishQuiz,

            section = existing.section,
            course = updatedCourses,
            batch = updatedBatches,
            student = updatedStudents,
            lab = updatedLabs,
            createdAt = existing.createdAt,
            createdBy = existing.createdBy
        )

        return updatedQuiz
    }

    fun editQuiz(dto: PatchQuizDTO, quizID : UUID) {

        val quiz = quizRepository.findById(quizID).orElseThrow { NotFoundException("Quiz not found") }

        val patchedQuiz = patchQuiz(
            existing = quiz,
            dto = dto,
            courseRepository = courseRepository,
            batchRepository = batchRepository,
            labRepository = labRepository,
            userRepository = userRepository
        )

         quizRepository.save(patchedQuiz)
    }
    fun addStudentToQuiz(quizId: UUID, studentId:List<String> ){
        val quiz = quizRepository.findById(quizId).orElseThrow{
            NotFoundException("quiz with id $quizRepository not found")
        }
        val student = userRepository.findAllById(studentId)
        quiz.student.addAll(student)
        quizRepository.save(quiz)
    }

    fun removeStudentFromQuiz(quizId: UUID, studentId:List<String> ){
        val quiz = quizRepository.findById(quizId).orElseThrow{
            NotFoundException("quiz with id $quizId not found")
        }
        val student = userRepository.findAllById(studentId)
        quiz.student.removeAll(student)
        quizRepository.save(quiz)
    }

    /**
     * Deletes a quiz by its ID
     * @param quizId The UUID of the quiz to delete
     * @throws NotFoundException if the quiz is not found
     */
    fun deleteQuiz(quizId: UUID) {
        if (!quizRepository.existsById(quizId)) {
            throw NotFoundException("Quiz with id $quizId not found")
        }
        quizRepository.deleteById(quizId)
    }
}
