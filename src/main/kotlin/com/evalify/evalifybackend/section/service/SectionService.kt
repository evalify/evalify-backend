package com.evalify.evalifybackend.section.service
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.service.QuizService
import com.evalify.evalifybackend.section.domain.DTO.GetSectionDTO
import com.evalify.evalifybackend.section.domain.Section
import com.evalify.evalifybackend.section.repository.SectionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.util.UUID

@Service
@Transactional

class SectionService(
    private val sectionRepository: SectionRepository,
    private val quizRepository: QuizRepository
) {
    private val logger = LoggerFactory.getLogger(SectionService::class.java)
    fun createNewSection(quizId : UUID, name : String) {
        try {
            val quiz = quizRepository.findById(quizId).orElseThrow { NotFoundException("Quiz not found") }
            val section = Section(
                quiz = quiz,
                name = name
            )
            sectionRepository.save(section)
        } catch (ex: Exception) {
            logger.error("Error creating new section for quizId=$quizId: ${ex.message}", ex)
            throw ex
        }
    }

    fun editSection(sectionId: UUID, name: String) {
        try {
            val section = sectionRepository.findById(sectionId).orElseThrow { NotFoundException("Section not found") }
            val newSection = Section(
                id = sectionId,
                name = name,
                quiz = section.quiz,
            )
            sectionRepository.save(newSection)
        } catch (ex: Exception) {
            logger.error("Error editing section sectionId=$sectionId: ${ex.message}", ex)
            throw ex
        }
    }

    fun deleteSection(sectionId: UUID){
        try {
            val section = sectionRepository.findById(sectionId).orElseThrow { NotFoundException("Section not found") }
            sectionRepository.deleteById(sectionId)
        } catch (ex: Exception) {
            logger.error("Error deleting section sectionId=$sectionId: ${ex.message}", ex)
            throw ex
        }
    }

    fun getSection(quizId: UUID) : GetSectionDTO{
        try {
            val quiz = quizRepository.findById(quizId).orElseThrow{ NotFoundException("Quiz with id $quizId not found!") }
            val section = quiz.section
            return GetSectionDTO(
                section = section
            )
        } catch (ex: Exception) {
            logger.error("Error getting section for quizId=$quizId: ${ex.message}", ex)
            throw ex
        }
    }

    fun getSectionQuestions(sectionId: UUID) : List<QuestionsReturnDTO> {
        try {
            val section = sectionRepository.findById(sectionId).orElseThrow { NotFoundException("Section not found") }
            val questions = section.quizQuestions
            return questions.map { question -> question.question.mapToType() }
        } catch (ex: Exception) {
            logger.error("Error getting questions for sectionId=$sectionId: ${ex.message}", ex)
            throw ex
        }
    }

    fun moveQuestions(sectionId: UUID, questions: List<QuizQuestion>, toSectionId:UUID) {
        try {
            val section = sectionRepository.findById(toSectionId).orElseThrow { NotFoundException("Section not found") }
            val fromSection = sectionRepository.findById(sectionId).orElseThrow { NotFoundException("Section not found") }
            fromSection.quizQuestions.removeAll(questions)
            sectionRepository.save(fromSection)

            val newQuestions = section.quizQuestions
            newQuestions.addAll(questions)
            section.quizQuestions.clear()
            section.quizQuestions.addAll(newQuestions)
            sectionRepository.save(section)
        } catch (ex: Exception) {
            logger.error("Error moving questions from sectionId=$sectionId to toSectionId=$toSectionId: ${ex.message}", ex)
            throw ex
        }
    }
}