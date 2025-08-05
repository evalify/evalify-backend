package com.evalify.evalifybackend.quiz.service
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.lab.repository.LabRepository
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.util.QuizValidationUtils
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class QuizLabService(private val labRepository:LabRepository, private val quizRepository: QuizRepository) {
    fun assignLabToQuiz(quizId:UUID,labId:List<UUID> ){
        val quiz = quizRepository.findById(quizId).orElseThrow{
            NotFoundException("quiz with id $labId not found")
        }

        QuizValidationUtils.validateQuizNotPublished(quiz)
        val lab = labRepository.findAllById(labId)
        quiz.lab.addAll(lab)
        quizRepository.save(quiz)
    }

    fun removeLabToQuiz(quizId:UUID,labId:List<UUID> ){
        val quiz = quizRepository.findById(quizId).orElseThrow{
            NotFoundException("quiz with id $labId not found")
        }
        QuizValidationUtils.validateQuizNotPublished(quiz)

        val lab = labRepository.findAllById(labId)
        quiz.lab.removeAll(lab)
        quizRepository.save(quiz)
    }


}

