package com.evalify.evalifybackend.quiz.service
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class PreviewQuizStudentService(private val quizRepository: QuizRepository,private val userRepository: UserRepository) {

    fun getStudentQuiz(studentId: String) {
        val student: User = userRepository.findById(studentId).orElseThrow{
            NotFoundException("Student $studentId Not Found")
        }

        //check with student id


    }
}