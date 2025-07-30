package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.FunctionParamDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO
import com.evalify.evalifybackend.bank.exception.BankNotFoundException
import com.evalify.evalifybackend.bank.exception.BankQuestionNotFoundException
import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.quiz.question.repository.BankQuestionRepository
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.quiz.question.repository.QuizQuestionRepository
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.crud.AddBankQuestionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.AddQuestionsResponse
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.CreateQuizQuestionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quizQuestionAddResponse
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.exception.QuizQuestionNotFoundException
import com.evalify.evalifybackend.quiz.filter.DifficultyLevelFilter
import com.evalify.evalifybackend.quiz.filter.ExistingQuestionFilter
import com.evalify.evalifybackend.quiz.filter.FilterManager
import com.evalify.evalifybackend.quiz.filter.QuestionTypeFilter
import com.evalify.evalifybackend.quiz.filter.TopicFilter
import com.evalify.evalifybackend.quiz.question.domain.CodingQuestion
import com.evalify.evalifybackend.quiz.question.domain.DescriptiveQuestion
import com.evalify.evalifybackend.quiz.question.domain.FileUpload
import com.evalify.evalifybackend.quiz.question.domain.FillUp.FillUp
import com.evalify.evalifybackend.quiz.question.domain.FillUp.blanks
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQ
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQOption
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MMCQ
import com.evalify.evalifybackend.quiz.question.domain.MCQ.TrueFalse
import com.evalify.evalifybackend.quiz.question.domain.MatchPair
import com.evalify.evalifybackend.quiz.question.domain.MatchTheFollowing
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.section.repository.SectionRepository
import com.evalify.evalifybackend.topic.repository.TopicRepo
import com.evalify.evalifybackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
open class QuizQuestionService(
    private val quizRepository: QuizRepository,
    private val bankRepository: BankRepository,
    private val quizQuestionRepository: QuizQuestionRepository,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository,
    private val topicRepo: TopicRepo,
    private val sectionRepository: SectionRepository,
    private val bankQuestionRepository: BankQuestionRepository,
    private val baseQuestionRepository : QuestionRepository
) {


    @Transactional
    open fun addByQuestionByFilters(
        quizId: UUID, topicId: List<UUID>?, difficulty: List<Difficulty>?, noOfQuestion: Int?,
        questionTypes: List<QuestionTypes>?, userId: String, bankIds: List<UUID>?
    ): List<BankQuestionsReturnDTO> {

        val quiz: Quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }


        val user = userRepository.findById(userId).orElseThrow{NotFoundException("User with id $userId not found")}

        val existingQuestionIds = quizQuestionRepository.findAll()
            .mapNotNull { it.bankQuestion?.id }
            .toSet()

        var count : Int

        val banks = if (bankIds != null) bankRepository.findAllById(bankIds) else bankRepository.findAll()


        val topics = if (topicId != null) topicRepo.findAllById(topicId) else topicRepo.findAll()


        val bankQuestions:List<BankQuestion> = banks.flatMap { bank -> bank.bankQuestion }
        val filterManager = FilterManager()
            .addFilter(ExistingQuestionFilter(existingQuestionIds = existingQuestionIds ))
            .addFilter(TopicFilter( topics = topics))
            .addFilter(DifficultyLevelFilter(difficult=difficulty))
            .addFilter(QuestionTypeFilter(questionTypes = questionTypes))

        val fQuestions:List<BankQuestion> = filterManager.applyFilter(bankQuestions)


        if(noOfQuestion != null)
        {
            count = noOfQuestion
            val message = if ((noOfQuestion ) > fQuestions.size) {
                count = fQuestions.size
                "Only ${fQuestions.size} questions added. Not enough questions are available."
            } else {
                "Successfully added ${fQuestions.size} questions to quiz."
            }
        }else{
            count = fQuestions.size
        }

        val filteredQuestions = fQuestions.shuffled()
            .filter { it.id !in existingQuestionIds } // <-- Add this line
            .take(count)
        val bankQuestionsId = filteredQuestions.map { bankQuestion -> bankQuestion.id }


        return filteredQuestions.map{bankQuestion ->
            bankQuestion.question.mapToBankType(bankQuestion.id)
        }
    }

    fun createQuestion(dto: CreateQuizQuestionDTO): BaseQuestion {
        val topics =if (!dto.topicIds.isNullOrEmpty())
        {
            topicRepo.findAllById(dto.topicIds)

        }
        else {
            emptyList()
        }
        val baseQuestion: BaseQuestion = when (dto.type) {
            QuestionTypes.MCQ ->   MCQ(
                id = null,
                question = dto.question,
                topic = topics.toMutableList(),
                explanation = dto.explanation,
                bank = null,
                hint = dto.hint,
                marks = dto.marks,
                bloomsTaxonomy = dto.bloomsTaxonomy,
                co = dto.co,
                negativeMark = dto.negativeMark,
                difficulty = dto.difficulty,
                options = dto.options?.map {
                    MCQOption(text = it.text, isCorrect = it.isCorrect)
                }?.toMutableList() ?: mutableListOf()
            )

            QuestionTypes.MMCQ ->   MMCQ(
                id = null,
                question = dto.question,
                bank = null,
                topic = topics.toMutableList(),
                explanation = dto.explanation,
                hint = dto.hint,
                marks = dto.marks,
                bloomsTaxonomy = dto.bloomsTaxonomy,
                co = dto.co,
                negativeMark = dto.negativeMark,
                difficulty = dto.difficulty,
                options = dto.options?.map {
                    MCQOption(text = it.text, isCorrect = it.isCorrect)
                }?.toMutableList() ?: mutableListOf()
            )

            QuestionTypes.CODING ->  CodingQuestion(
                id = null,
                question = dto.question,
                bank = null,
                topic = topics.toMutableList(),
                explanation = dto.explanation,
                hint = dto.hint,
                marks = dto.marks,
                bloomsTaxonomy = dto.bloomsTaxonomy,
                co = dto.co,
                negativeMark = dto.negativeMark,
                difficulty = dto.difficulty,
                driverCode = dto.driverCode,
                boilerCode = dto.boilerCode,

                testcases = dto.testcases?.map { TestCaseDTO(it.code,it.tags,it.isMinimal,it.language) } ?: listOf(),
                language = dto.language,
            )

            QuestionTypes.FILL_UP -> FillUp(
                id = null,
                question = dto.question,
                bank = null,
                topic = topics.toMutableList(),
                explanation = dto.explanation,
                hint = dto.hint,
                marks = dto.marks,
                bloomsTaxonomy = dto.bloomsTaxonomy,
                co = dto.co,
                negativeMark = dto.negativeMark,
                difficulty = dto.difficulty,
                strictMatch = dto.strictMatch,
                llmEval = dto.llmEval,
                blanks = dto.blanks?.map { blanks(answers = it.answers, type = it.type) } ?: listOf()
            )

            QuestionTypes.DESCRIPTIVE  -> DescriptiveQuestion(
                id = null,
                question = dto.question,
                bank = null,
                topic = topics.toMutableList(),
                explanation = dto.explanation,
                hint = dto.hint,
                marks = dto.marks,
                bloomsTaxonomy = dto.bloomsTaxonomy,
                co = dto.co,
                negativeMark = dto.negativeMark,
                difficulty = dto.difficulty,
                expectedAnswer = dto.expectedAnswer,
                strictness = dto.strictness,
                guidelines = dto.guidelines,
            )

            QuestionTypes.FILE_UPLOAD  -> FileUpload(
                id = null,
                question = dto.question,
                bank = null,
                topic = topics.toMutableList(),
                explanation = dto.explanation,
                hint = dto.hint,
                marks = dto.marks,
                bloomsTaxonomy = dto.bloomsTaxonomy,
                co = dto.co,
                negativeMark = dto.negativeMark,
                difficulty = dto.difficulty,
                link = dto.link
            )

            QuestionTypes.MATCH_THE_FOLLOWING -> MatchTheFollowing(
                id = null,
                question = dto.question,
                bank = null,
                topic = topics.toMutableList(),
                explanation = dto.explanation,
                hint = dto.hint,
                marks = dto.marks,
                bloomsTaxonomy = dto.bloomsTaxonomy,
                co = dto.co,
                negativeMark = dto.negativeMark,
                difficulty = dto.difficulty,
                keys = dto.keys,
                values = dto.values,
                matchPair = dto.matchPairs

            )

            QuestionTypes.TRUEFALSE  -> TrueFalse(
                id = null,
                question = dto.question,
                bank = null,
                topic = topics.toMutableList(),
                explanation = dto.explanation,
                hint = dto.hint,
                marks = dto.marks,
                bloomsTaxonomy = dto.bloomsTaxonomy,
                co = dto.co,
                negativeMark = dto.negativeMark,
                difficulty = dto.difficulty,
                answer = dto.trueFalseAnswer ?: false
            )

        }

        return baseQuestion
    }


    @Transactional
    fun createQuizQuestion(dto: CreateQuizQuestionDTO, quizId: UUID,userId:String?) {

        if(userId == null) throw RuntimeException("User id cannot be null")
        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        val section = sectionRepository.findById(dto.sectionId).orElseThrow { RuntimeException("Section not found") }

        val quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }

        val baseQuestion = createQuestion(dto)
        val savedQuestion = questionRepository.save(baseQuestion)

        val quizQuestion = QuizQuestion(
            question = savedQuestion,
            section = section,
            updateBy = user
        )

        quizQuestionRepository.save(quizQuestion)
        section.quizQuestions.add(quizQuestion)
        sectionRepository.save(section)
    }
    @Transactional
    fun addSelectQuestions(quizId: UUID, dto: AddBankQuestionDTO,userId:String?) : quizQuestionAddResponse {

        if(userId == null) throw RuntimeException("User id cannot be null")

        val quiz: Quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }
        val section = sectionRepository.findById(dto.sectionId).orElseThrow{
            NotFoundException("Quiz with id ${dto.sectionId} not found")
        }

        val user = userRepository.findById(userId).orElseThrow{NotFoundException("User with id $userId not found")}

        val existingQuestionIds: Set<UUID?> = quizQuestionRepository.findAll()
            .mapNotNull { it.bankQuestion?.id }
            .toSet()

        val finalQuestions : MutableList<BankQuestion> = mutableListOf()
        val bankQuestions = bankQuestionRepository.findAllById(dto.bankQuestionId)

        bankQuestions.forEach { bankQuestion ->
            if(bankQuestion.id !in existingQuestionIds){
                finalQuestions.add(bankQuestion)
            }
        }

        finalQuestions.forEach { bankQuestion ->

            val dupQuestion = bankQuestion.question.copyQuestion()

            val duplicatedQuestion = questionRepository.save(dupQuestion)

            val quizQuestions = QuizQuestion(
                question = duplicatedQuestion,
                section = section,
                updateBy = user,
                bankQuestion = bankQuestion
            )

            val savedQuizQuestion = quizQuestionRepository.save(quizQuestions)
            section.quizQuestions.add(savedQuizQuestion)
            sectionRepository.save(section)



        }
        return quizQuestionAddResponse(
            quizId = quizId,
            sectionId = dto.sectionId,
            message = "Question added successfully",
        )
}
    @Transactional
    fun editQuizQuestion(quizId: UUID, questionId: UUID, patchDTO: PatchQuestionDTO, userId: String?) {
        if (userId == null) throw RuntimeException("User id cannot be null")

        val user = userRepository.findById(userId).orElseThrow {
            NotFoundException("User with id $userId not found")
        }

        val quizQuestion = quizQuestionRepository.findById(questionId).orElseThrow{
            QuizQuestionNotFoundException(questionId.toString())
        }
        val baseQuestion = quizQuestion.question
        val updatedQuestion = baseQuestion.patchWith(patchDTO,topicRepo)
            ?: throw IllegalArgumentException("Failed to patch question")
        val savedBaseQuestion = questionRepository.save(updatedQuestion)
        val updatedQuizQuestion = QuizQuestion(
            id = questionId,
            question = savedBaseQuestion,
            updateBy = user,
            updatedAt = java.time.Instant.now(),
            section = quizQuestion.section

        )
        baseQuestionRepository.save(savedBaseQuestion)
        quizQuestionRepository.save(updatedQuizQuestion)

    }

    @Transactional
    fun deleteQuizQuestion(quizId: UUID, questionId: UUID) {

        val quizQuestion = quizQuestionRepository.findById(questionId).orElseThrow{
            QuizQuestionNotFoundException(questionId.toString())
        }

        val quizQuestionId = quizQuestion.id ?: throw RuntimeException("Quiz question id cannot be null")

        val section = quizQuestion.section ?: throw RuntimeException("Section cannot be null")
        section.quizQuestions.removeIf { it.id == quizQuestionId }
        sectionRepository.save(section)

        val baseQuestionId = quizQuestion.question.id ?: throw RuntimeException("Base question id cannot be null")

        quizQuestionRepository.deleteById(quizQuestionId)
        baseQuestionRepository.deleteById(baseQuestionId)



    }


}





