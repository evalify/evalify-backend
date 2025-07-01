package com.evalify.evalifybackend.quiz.service


import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.lab.repository.LabRepository
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.quiz.domain.DTO.criteria.SelectionCriteriaDTO

import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.CreateQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.PatchQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.criteria.PermutationsDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.ShareQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedTags
import com.evalify.evalifybackend.quiz.domain.QuizSet
import com.evalify.evalifybackend.quiz.domain.QuizSetQuestion
import com.evalify.evalifybackend.quiz.domain.QuizUser
import com.evalify.evalifybackend.quiz.domain.QuizUserId
import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizSetRepository
import com.evalify.evalifybackend.topic.repository.TopicRepo
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.String
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import com.evalify.evalifybackend.quiz.util.CombinationUtils
import org.apache.commons.lang3.stream.IntStreams.range
import org.springframework.beans.factory.annotation.Autowired


@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val batchRepository: BatchRepository,
    private val labRepository: LabRepository,
    private val quizSetRepository: QuizSetRepository,
    private val topicRepo: TopicRepo

) {


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

           )
        val quizUser = QuizUser(
            id = QuizUserId(quiz.id,user.id),
            quiz = quiz,
            user = user,
            tags = SharedTags.OWNER
        )
        quiz.sharedUsers.add(quizUser)

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

    fun publishQuiz(quizId : UUID, noSets : Int,dto : SelectionCriteriaDTO)  {
        val quiz = quizRepository.findById(quizId).orElseThrow { NotFoundException("Quiz not found") }
        val allQuestions: List<BaseQuestion> = quiz.section
            .flatMap { it.quizQuestions }
            .mapNotNull { it.question }

        val grouped: Map<UUID, Map<String, List<BaseQuestion>>> = allQuestions
            .flatMap { question ->
                question.topic.map { topic -> topic.id!! to question }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, questions) ->
                questions.groupBy { it.difficulty.name.lowercase() }
            }

        val topicDifficultyCombos = mutableListOf<List<List<BaseQuestion>>>()

        for (topic in dto.criteria) {
            val topicGroup = grouped[topic.topicId]
                ?: throw IllegalStateException("No questions found for topic ${topic.topicId}")

            fun getCombinations(diff: String, count: Int): List<List<BaseQuestion>> {
                val qList = topicGroup[diff] ?: emptyList()
                if (qList.size < count) throw IllegalStateException("Insufficient questions for $diff in topic ${topic.topicId}")
                return CombinationUtils.combinations(qList, count)
            }

            val easyCombs = getCombinations("easy", topic.easy)
            val medCombs = getCombinations("medium", topic.medium)
            val hardCombs = getCombinations("hard", topic.hard)

            topicDifficultyCombos.add(easyCombs)
            topicDifficultyCombos.add(medCombs)
            topicDifficultyCombos.add(hardCombs)
        }

        val allCombinations = CombinationUtils.cartesianProduct(topicDifficultyCombos)

        val validSets = allCombinations
            .map { set -> set.flatten() }
            .filter { questions -> questions.sumOf { it.marks } == dto.totalMarks }
            .distinctBy { it.mapNotNull { q -> q.id }.sorted() }

        if (validSets.size < noSets)
            throw IllegalStateException("Only ${validSets.size} valid sets available, but $noSets requested")


        val publishedQuiz = quiz.publishQuiz(noSets)
        val updatedQuiz = quizRepository.save(publishedQuiz)

        for (i in 0 until noSets) {
            val set = validSets[i]

            val quizSet = QuizSet(
                setNumber = i,
                quiz = updatedQuiz,

            )

            val quizSetQuestions = set.mapIndexed { order, question ->
                QuizSetQuestion(
                    quizSet = quizSet,
                    question = question,
                    order = order
                )
            }

            quizSet.questions.addAll(quizSetQuestions)
            quizSetRepository.save(quizSet)
        }

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

    fun shareQuiz(quizID: UUID, shareDTO: ShareQuizDTO) {

        val quiz = quizRepository.findById(quizID).orElseThrow { NotFoundException("Quiz with id $quizID not found") }

        val user = userRepository.findAllById(shareDTO.userID)

        user.map{
            user->
            val quizUser = QuizUser(
                id =  QuizUserId(quizID,user.id),
                quiz = quiz,
                user = user,
                tags = SharedTags.SHARED
            )
            quiz.sharedUsers.add(quizUser)
            quizRepository.save(quiz)

           }


    }

    fun checkAvailability(quizId: UUID, dto: SelectionCriteriaDTO): PermutationsDTO {
        val quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }

        // Step 1: Flatten all BaseQuestions from the quiz
        val allQuestions: List<BaseQuestion> = quiz.section
            .flatMap { it.quizQuestions }
            .mapNotNull { it.question }

        // Step 2: Group by each topic ID → then by difficulty
        val grouped: Map<UUID, Map<String, List<BaseQuestion>>> = allQuestions
            .flatMap { question ->
                question.topic.map { topic -> topic.id!! to question } // each topic ID points to the question
            }
            .groupBy({ it.first }, { it.second }) // Group by topicId
            .mapValues { (_, questions) ->
                questions.groupBy { it.difficulty.name.lowercase() } // group by difficulty string
            }

        val topicDifficultyCombos = mutableListOf<List<List<BaseQuestion>>>()

        for (topic in dto.criteria) {
            val topicGroup = grouped[topic.topicId] ?: return PermutationsDTO(false, 0)

            fun getCombinations(diff: String, count: Int): List<List<BaseQuestion>> {
                val qList = topicGroup[diff] ?: return emptyList()
                if (qList.size < count) return emptyList()
                return CombinationUtils.combinations(qList, count)
            }

            val easyCombs = getCombinations("easy", topic.easy)
            val medCombs = getCombinations("medium", topic.medium)
            val hardCombs = getCombinations("hard", topic.hard)

            if (easyCombs.isEmpty() || medCombs.isEmpty() || hardCombs.isEmpty())
                return PermutationsDTO(false, 0)

            topicDifficultyCombos.add(easyCombs)
            topicDifficultyCombos.add(medCombs)
            topicDifficultyCombos.add(hardCombs)
        }

        // Step 3: Cartesian product of all topic-difficulty combinations
        val allCombinations = CombinationUtils.cartesianProduct(topicDifficultyCombos)

        // Step 4: Filter only those combinations whose total marks == dto.totalMarks
        val validSets = allCombinations.filter { set ->
            set.flatten().sumOf { it.marks } == dto.totalMarks
        }.map { set ->
            set.flatten().mapNotNull { it.id }.sorted()
        }.toSet()

        // Step 5: Count unique permutations
        val totalPerms = validSets.sumOf { ids ->
            val freq = ids.groupingBy { it }.eachCount()
            val numerator = CombinationUtils.factorial(ids.size)
            val denominator = freq.values.fold(1L) { acc, count -> acc * CombinationUtils.factorial(count) }
            numerator / denominator
        }

        return PermutationsDTO(validSets.isNotEmpty(), totalPerms.toInt())
    }}


