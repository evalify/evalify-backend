package com.evalify.evalifybackend.section.service
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.service.QuizService
import com.evalify.evalifybackend.section.domain.DTO.GetSectionDTO
import com.evalify.evalifybackend.section.domain.Section
import com.evalify.evalifybackend.section.repository.SectionRepository
import com.evalify.evalifybackend.section.exception.*
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
        // Validate section name
        if (name.isBlank()) {
            throw SectionValidationException("Section name cannot be blank")
        }

        try {
            val quiz = quizRepository.findById(quizId).orElseThrow { 
                QuizNotFoundException(quizId.toString()) 
            }
            val section = Section(
                quiz = quiz,
                name = name
            )
            sectionRepository.save(section)
        } catch (e: Exception) {
            when (e) {
                is QuizNotFoundException, is SectionValidationException -> throw e
                else -> {
                    logger.error("Error creating new section for quizId=$quizId: ${e.message}", e)
                    throw SectionServiceException("Failed to create section", e)
                }
            }
        }
    }

    fun editSection(sectionId: UUID, name: String) {
        // Validate section name
        if (name.isBlank()) {
            throw SectionValidationException("Section name cannot be blank")
        }

        try {
            val section = sectionRepository.findById(sectionId).orElseThrow { 
                SectionNotFoundException(sectionId.toString()) 
            }
            val newSection = Section(
                id = sectionId,
                name = name,
                quiz = section.quiz,
            )
            sectionRepository.save(newSection)
        } catch (e: Exception) {
            when (e) {
                is SectionNotFoundException, is SectionValidationException -> throw e
                else -> {
                    logger.error("Error editing section sectionId=$sectionId: ${e.message}", e)
                    throw SectionServiceException("Failed to edit section", e)
                }
            }
        }
    }

    fun deleteSection(sectionId: UUID){
        try {
            val section = sectionRepository.findById(sectionId).orElseThrow { 
                SectionNotFoundException(sectionId.toString()) 
            }
            sectionRepository.deleteById(sectionId)
        } catch (e: Exception) {
            when (e) {
                is SectionNotFoundException -> throw e
                else -> {
                    logger.error("Error deleting section sectionId=$sectionId: ${e.message}", e)
                    throw SectionServiceException("Failed to delete section", e)
                }
            }
        }
    }

    fun getSection(quizId: UUID) : GetSectionDTO{
        try {
            val quiz = quizRepository.findById(quizId).orElseThrow{ 
                QuizNotFoundException(quizId.toString()) 
            }
            val section = quiz.section
            return GetSectionDTO(
                section = section
            )
        } catch (e: Exception) {
            when (e) {
                is QuizNotFoundException -> throw e
                else -> {
                    logger.error("Error getting section for quizId=$quizId: ${e.message}", e)
                    throw SectionServiceException("Failed to get section", e)
                }
            }
        }
    }

    fun getSectionQuestions(sectionId: UUID) : List<QuestionsReturnDTO> {
        try {
            val section = sectionRepository.findById(sectionId).orElseThrow { 
                SectionNotFoundException(sectionId.toString()) 
            }
            val questions = section.quizQuestions
            return questions.map { question -> question.question.mapToType() }
        } catch (e: Exception) {
            when (e) {
                is SectionNotFoundException -> throw e
                else -> {
                    logger.error("Error getting questions for sectionId=$sectionId: ${e.message}", e)
                    throw SectionServiceException("Failed to get section questions", e)
                }
            }
        }
    }

    fun moveQuestions(sectionId: UUID, questions: List<QuizQuestion>, toSectionId:UUID) {
        try {
            val section = sectionRepository.findById(toSectionId).orElseThrow { 
                SectionNotFoundException(toSectionId.toString()) 
            }
            val fromSection = sectionRepository.findById(sectionId).orElseThrow { 
                SectionNotFoundException(sectionId.toString()) 
            }
            
            // Validate that questions are not empty
            if (questions.isEmpty()) {
                throw SectionValidationException("No questions provided to move")
            }
            
            fromSection.quizQuestions.removeAll(questions)
            sectionRepository.save(fromSection)

            val newQuestions = section.quizQuestions
            newQuestions.addAll(questions)
            section.quizQuestions.clear()
            section.quizQuestions.addAll(newQuestions)
            sectionRepository.save(section)
        } catch (e: Exception) {
            when (e) {
                is SectionNotFoundException, is SectionValidationException -> throw e
                else -> {
                    logger.error("Error moving questions from sectionId=$sectionId to toSectionId=$toSectionId: ${e.message}", e)
                    throw QuestionMoveException(sectionId.toString(), toSectionId.toString(), e)
                }
            }
        }
    }
    }
