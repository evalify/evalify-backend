package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.FunctionParamDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO
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
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
open class QuizQuestionService(
    val quizRepository: QuizRepository,
    val bankRepository: BankRepository,
    val quizQuestionRepository: QuizQuestionRepository,
    val questionRepository: QuestionRepository,
    val userRepository: UserRepository,
    val topicRepo: TopicRepo,
    val sectionRepository: SectionRepository,
    private val bankQuestionRepository: BankQuestionRepository
) {


    @Transactional
    open fun addByQuestionByFilters(
        quizId: UUID, topicId: List<UUID>?, difficulty: List<Difficulty>?, noOfQuestion: Int?,
        questionTypes: List<QuestionTypes>?, userId: String, bankIds: List<UUID>?,sectionId : UUID
    ): AddQuestionsResponse {


        val filteredTopics: MutableList<BankQuestion> = mutableListOf()
        val filteredLevels: MutableList<BankQuestion> = mutableListOf()
        val filteredQuestionTypes: MutableList<BankQuestion> = mutableListOf()

        val quiz: Quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }
        val section = sectionRepository.findById(sectionId).orElseThrow{
            NotFoundException("Quiz with id $sectionId not found")
        }

        val user = userRepository.findById(userId).orElseThrow{NotFoundException("User with id $userId not found")}


        // for unique addition to the quiz from the bank questions
        val existingQuestionIds: Set<UUID?> = section.quizQuestions.map{ it.bankQuestion?.id }.toSet()

        var count : Int



        val banks = if (bankIds != null) {
            bankRepository.findAllById(bankIds)
        } else {
            bankRepository.findAll()
        }

        val topics = if (topicId != null) {
            topicRepo.findAllById(topicId)
        } else {
            topicRepo.findAll()
        }
        banks.forEach { bank ->
            val questions = bank.bankQuestion.filter { bankQuestion ->
                bankQuestion.id !in existingQuestionIds &&
                        bankQuestion.question.topic.any { it in topics }
            }
            filteredTopics.addAll(questions)
        }


        if (difficulty != null) {
            filteredLevels.addAll(filteredTopics.filter { bankQuestion ->
                bankQuestion.question.difficulty in difficulty
            })
        } else {
            filteredLevels.addAll(filteredTopics)
        }

        if (questionTypes != null) {

            filteredQuestionTypes.addAll(filteredLevels.filter { bankQuestion ->
                bankQuestion.question.getQuestionType() in questionTypes
            })
        } else {
            filteredQuestionTypes.addAll(filteredLevels)
        }

        if(noOfQuestion != null)
        {
            count = noOfQuestion
            val message = if ((noOfQuestion ) > filteredQuestionTypes.size) {
                count = filteredQuestionTypes.size
                "Only ${filteredQuestionTypes.size} questions added. Not enough questions are available."
            } else {
                "Successfully added ${filteredQuestionTypes.size} questions to quiz."
            }
        }else{
            count = filteredQuestionTypes.size
        }



        val filteredQuestions = filteredQuestionTypes.shuffled().take(count)
        val bankQuestionsId = filteredQuestions.map {bankQuestion -> bankQuestion.id}

        filteredQuestions.forEach { bankQuestion ->

            //Creating a quiz question
            val dupQuestion = bankQuestion.question.copyQuestion()

            //duplicating a question
            val duplicatedQuestion = questionRepository.save(dupQuestion)

            //creating a quiz question object
            val quizQuestions = QuizQuestion(
                question = duplicatedQuestion,
                section = section,
                updateBy = user,
                bankQuestion = bankQuestion
            )
            //saving to quiz question table
            val savedQuizQuestion = quizQuestionRepository.save(quizQuestions)
            section.quizQuestions.add(savedQuizQuestion)
        }

            sectionRepository.save(section)

        return AddQuestionsResponse(
            quizId = quiz.id,
            sectionId = sectionId,
            addedQuestionCount = filteredQuestions.size,
            addedBankQuestionIds = bankQuestionsId,
            message = "Questions added successfully"
        )
    }

    fun createQuestion(dto: CreateQuizQuestionDTO): BaseQuestion {
        val topics = topicRepo.findAllById(dto.topicIds)

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
                functionName = dto.functionName,
                returnType = dto.returnType,
                params = dto.params?.map { FunctionParamDTO(it.param, it.type) } ?: listOf(),
                testcases = dto.testcases?.map { TestCaseDTO(it.input, it.expected) } ?: listOf(),
                language = dto.language,
                answer = dto.answer
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
                template = dto.template,
                blanks = dto.blanks?.map { blanks(it.id, it.answers) } ?: listOf()
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
                answer = dto.answer
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
                expectedAnswer = dto.expectedAnswer,
                strictness = dto.strictness,
                guidelines = dto.guidelines
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
                keys = dto.keys?.map {
                    MatchPair(
                        id = UUID.randomUUID().toString(),
                        leftPair = it.leftPair,
                        rightPair = it.rightPair
                    )
                }?.toMutableList() ?: mutableListOf()
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

    fun addSelectQuestions(quizId: UUID, dto: AddBankQuestionDTO,userId:String?) : quizQuestionAddResponse {

        if(userId == null) throw RuntimeException("User id cannot be null")

        val quiz: Quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }
        val section = sectionRepository.findById(dto.sectionId).orElseThrow{
            NotFoundException("Quiz with id ${dto.sectionId} not found")
        }

        val user = userRepository.findById(userId).orElseThrow{NotFoundException("User with id $userId not found")}

        val existingQuestionIds: Set<UUID?> = section.quizQuestions.map{ it.bankQuestion?.id }.toSet()

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
}





