package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.AccessDTO
import com.evalify.evalifybackend.bank.domain.DTO.BankDetailsDTO
import com.evalify.evalifybackend.bank.domain.DTO.CreateQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.FunctionParamDTO
import com.evalify.evalifybackend.bank.domain.DTO.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.ReturnBankQuestionsDTO
import com.evalify.evalifybackend.bank.domain.DTO.ReturnTopicDTO
import com.evalify.evalifybackend.bank.domain.DTO.TestCaseDTO
import com.evalify.evalifybackend.bank.domain.DTO.TopicDTO
import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.question.domain.CodingQuestion
import com.evalify.evalifybackend.quiz.question.domain.DescriptiveQuestion
import com.evalify.evalifybackend.quiz.question.domain.FileUpload
import com.evalify.evalifybackend.quiz.question.domain.FillUp.FillUp
import com.evalify.evalifybackend.quiz.question.domain.FillUp.blanks
import com.evalify.evalifybackend.quiz.question.domain.FunctionParam
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQ
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQOption
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MMCQ
import com.evalify.evalifybackend.quiz.question.domain.MCQ.TrueFalse
import com.evalify.evalifybackend.quiz.question.domain.MatchPair
import com.evalify.evalifybackend.quiz.question.domain.MatchTheFollowing
import com.evalify.evalifybackend.quiz.question.domain.TestCase
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.quiz.question.repository.BankQuestionRepository
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.topic.repository.TopicRepo
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class BankManagerService(
    private val bankRepository: BankRepository, private val userRepository: UserRepository
    , private val topicRepo: TopicRepo, private val baseQuestionRepository: QuestionRepository, private val bankQuestionRepository: BankQuestionRepository
) {

    @Transactional
    fun getDetailsOfBank(): MutableList<BankDetailsDTO> {
        val banks = bankRepository.findAll()

        return banks.map { bank ->
            BankDetailsDTO(
                id = bank?.id,
                courseCode = bank.courseCode,
                name = bank.name,
                semester = "S${bank.semester}",
                questions = bank.bankQuestion.size,
                topics = bank.topics?.size,
                access = bank.sharedUser.map { AccessDTO(it.id,it.name,it.email) } // or it.id, or build DTO
            )
        }.toMutableList()
    }

    @Transactional
    fun getBankInfo(bankId:UUID): BankDetailsDTO {
        val bank = bankRepository.findById(bankId).orElseThrow { NotFoundException("Bank with ID $bankId not found") }

        return BankDetailsDTO(
            id = bank?.id,
            courseCode = bank.courseCode,
            name = bank.name,
            semester = "S${bank.semester}",
            questions = bank.bankQuestion.size,
            topics = bank.topics?.size,
            access = bank.sharedUser.map { AccessDTO(it.id,it.name,it.email) }
        )
    }

    @Transactional
    fun getBankQuestions(bankId: UUID) : ReturnBankQuestionsDTO {
        val bank = bankRepository.findByIdWithQuestions(bankId)


        val bankQuestions = bank.bankQuestion
        val topics = bank.topics

        return ReturnBankQuestionsDTO(
            questions = bankQuestions,
            topics = topics?.map { TopicDTO(it.id, it.name) }
        )
    }

    @Transactional
    fun getBankTopics(bankId: UUID): List<ReturnTopicDTO>? {
        val bank = bankRepository.findById(bankId).orElseThrow { RuntimeException("Bank not found") }
        val topics = bank.topics

        return topics?.map { topic -> ReturnTopicDTO(id = topic.id, name = topic.name) }


    }
    @Transactional
    fun getQuestionsByTopic(topic: List<UUID>, bankId: UUID): List<BankQuestion> {

        val topics = topicRepo.findAllById(topic)
        val bank = bankRepository.findById(bankId).orElseThrow { RuntimeException("Bank not found") }
        return bank.bankQuestion.filter { bankQ ->
            bankQ.question.topic.any { it in topics }
        }

    }

    fun createQuestion(dto: CreateQuestionDTO, bankId: UUID): BaseQuestion {
        val bank = bankRepository.findById(bankId).orElseThrow() { RuntimeException("Bank not found") }
        val topics = topicRepo.findAllById(dto.topicIds)

        val baseQuestion: BaseQuestion = when (dto.type) {
            QuestionTypes.MCQ ->   MCQ(
                id = null,
                question = dto.question,
                bank = bank,
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

            QuestionTypes.MMCQ ->   MMCQ(
                id = null,
                question = dto.question,
                bank = bank,
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
                bank = bank,
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
                bank = bank,
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
                bank = bank,
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

            QuestionTypes.FILE_UPLOAD  -> FileUpload(
                id = null,
                question = dto.question,
                bank = bank,
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
                bank = bank,
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
                bank = bank,
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

    fun createBankQuestion(dto : CreateQuestionDTO , bankId : UUID , userId : String?){

        if(userId == null) throw RuntimeException("User id cannot be null")
        val baseQuestion = createQuestion(dto, bankId)
        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        val bank = bankRepository.findById(bankId).orElseThrow { RuntimeException("Bank not found") }
        val savedQuestion = baseQuestionRepository.save(baseQuestion)

        val bankQuestion = BankQuestion(

            question = savedQuestion,
            updateBy = user

        )

        bankQuestionRepository.save(bankQuestion)


    }

    fun editBankQuestion(dto : CreateQuestionDTO , bankId : UUID , questionId : UUID , userId : String?){

        if(userId == null) throw RuntimeException("User id cannot be null")
        val bankQuestion = bankQuestionRepository.findById(questionId).orElseThrow { RuntimeException("Question not found")}
        val user = userRepository.findById(userId).orElseThrow { NotFoundException("User not found") }
        val baseQuestionId = bankQuestion.question.id ?: throw NotFoundException("Question id cannot be null")

        val updatedBaseQuestion = createQuestion(dto, bankId)

        if (bankQuestion.question::class != updatedBaseQuestion::class)
            throw IllegalArgumentException("Cannot change question type")

        updatedBaseQuestion.id = baseQuestionId

        val savedQuestion = baseQuestionRepository.save(updatedBaseQuestion)
        val bankQuestions = BankQuestion(

            question = savedQuestion,
            updateBy = user

        )

        bankQuestionRepository.save(bankQuestions)

    }




    fun deleteBankQuestion(questionId : UUID ){

        val bankQuestion = bankQuestionRepository.findById(questionId).orElseThrow { NotFoundException("Question not found") }
        val baseQuestionId = bankQuestion.question.id ?: throw NotFoundException("Question id cannot be null")

        baseQuestionRepository.deleteById(baseQuestionId)
        bankQuestionRepository.deleteById(questionId)

    }


}