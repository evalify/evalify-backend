package com.evalify.evalifybackend.quiz.service
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class QuizCourseService(private val courseRepository:CourseRepository, private val quizRepository: QuizRepository) {
     fun assignCourseToQuiz(quizId:UUID,courseId:List<UUID> ){
        val quiz = quizRepository.findById(quizId).orElseThrow{
            NotFoundException("quiz with id $quizId not found")
        }
        val course = courseRepository.findAllById(courseId)
        quiz.course.addAll(course)
        quizRepository.save(quiz)
    }

    fun removeCourseFromQuiz(quizId:UUID,courseId:List<UUID> ){
        val quiz = quizRepository.findById(quizId).orElseThrow{
            NotFoundException("quiz with id $quizId not found")
        }
        val course = courseRepository.findAllById(courseId)
        quiz.course.removeAll(course)
        quizRepository.save(quiz)
    }


}