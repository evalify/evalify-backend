package com.evalify.evalifybackend.quiz.service
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.lab.repository.LabRepository
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class QuizLabService(private val labRepository:LabRepository, private val quizRepository: QuizRepository) {
    fun assignLabToQuiz(quizId:UUID,labId:List<UUID> ){
        val quiz = quizRepository.findById(quizId).orElseThrow{
            NotFoundException("quiz with id $labId not found")
        }
        val lab = labRepository.findAllById(labId)
        quiz.lab.addAll(lab)
        quizRepository.save(quiz)
    }

    fun removeLabToQuiz(quizId:UUID,labId:List<UUID> ){
        val quiz = quizRepository.findById(quizId).orElseThrow{
            NotFoundException("quiz with id $labId not found")
        }
        val lab = labRepository.findAllById(labId)
        quiz.lab.removeAll(lab)
        quizRepository.save(quiz)
    }


}