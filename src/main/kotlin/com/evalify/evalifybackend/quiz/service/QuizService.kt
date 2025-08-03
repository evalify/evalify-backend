package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.lab.repository.LabRepository
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.exception.QuestionNotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.criteria.PermutationsDTO
import com.evalify.evalifybackend.quiz.domain.DTO.criteria.SelectionCriteriaDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.CreateQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.PatchQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.CourseInfoDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizPreviewDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.ShareQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedQuizPreviewDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedTags
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedUserDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SimpleUserDTO
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizSet
import com.evalify.evalifybackend.quiz.domain.QuizSetQuestion
import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.evalify.evalifybackend.quiz.domain.QuizTags
import com.evalify.evalifybackend.quiz.domain.QuizUser
import com.evalify.evalifybackend.quiz.domain.QuizUserId
import com.evalify.evalifybackend.quiz.exception.QuizBusinessLogicException
import com.evalify.evalifybackend.quiz.exception.QuizDatabaseException
import com.evalify.evalifybackend.quiz.exception.QuizNotFoundException
import com.evalify.evalifybackend.quiz.exception.QuizValidationException
import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.quiz.question.repository.QuizQuestionRepository
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizSetRepository
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.quiz.repository.QuizTagsRepository
import com.evalify.evalifybackend.quiz.util.CombinationUtils
import com.evalify.evalifybackend.quiz.util.QuizSecurityUtils
import com.evalify.evalifybackend.quiz.util.QuizValidationUtils
import com.evalify.evalifybackend.section.repository.SectionRepository
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import com.evalify.evalifybackend.topic.repository.TopicRepo
import com.evalify.evalifybackend.user.repository.UserRepository
import java.util.*
import kotlin.String
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import org.springframework.dao.DataAccessException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


@Service
@Transactional
class QuizService(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val batchRepository: BatchRepository,
    private val labRepository: LabRepository,
    private val quizSetRepository: QuizSetRepository,
    private val topicRepo: TopicRepo,
    private val quizTagsRepository: QuizTagsRepository,
    private val semesterRepository: SemesterRepository,
    private val sectionRepository: SectionRepository,
    private val quizQuestionRepository: QuizQuestionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val quizStudentRepository: QuizStudentRepository
) {

    private val logger by logger()

    @Transactional
    fun createQuiz(quizDTO: CreateQuizDTO, userId: String?): UUID {
        logger.info("Creating quiz '{}' for user: {}", quizDTO.name, userId)

        try {
            // Validate input
            if (userId == null) {
                throw QuizValidationException("User ID cannot be null", "userId")
            }

            QuizValidationUtils.validateCreateQuizData(quizDTO)

            // Fetch and validate entities
            val user =
                    userRepository.findById(userId).orElseThrow {
                        NotFoundException("User with ID $userId not found")
                    }

            val courses = if(quizDTO.courseIds.isEmpty()) emptyList() else courseRepository.findAllById(quizDTO.courseIds)

            if (courses.size != quizDTO.courseIds.size) {
                val foundIds = courses.map { it.id }
                val missingIds = quizDTO.courseIds.filterNot { foundIds.contains(it) }
                throw NotFoundException("Courses not found: $missingIds")
            }

            val batches = if(quizDTO.batchIds.isEmpty()) emptyList() else batchRepository.findAllById(quizDTO.batchIds)
            if (batches.size != quizDTO.batchIds.size) {
                val foundIds = batches.map { it.id }
                val missingIds = quizDTO.batchIds.filterNot { foundIds.contains(it) }
                throw NotFoundException("Batches not found: $missingIds")
            }


            val labs = if(quizDTO.labIds.isEmpty()) emptyList() else labRepository.findAllById(quizDTO.labIds)
            if (labs.size != quizDTO.labIds.size) {
                val foundIds = labs.map { it.id }
                val missingIds = quizDTO.labIds.filterNot { foundIds.contains(it) }
                throw NotFoundException("Labs not found: $missingIds")
            }

            // Determine students
            val students = if(quizDTO.studentIds.isEmpty()) emptyList() else userRepository.findAllById(quizDTO.studentIds)
                        if (students.size != quizDTO.studentIds.size) {
                            val foundIds = students.map { it.id }
                            val missingIds = quizDTO.studentIds.filterNot { foundIds.contains(it) }
                            throw NotFoundException("Students not found: $missingIds")
                        }

            val semestersManaged = semesterRepository.findByManagerId(listOf(user))
            val validTags = semestersManaged.flatMap { it.quizTags }.distinct()
            // Check if all requested quizTags are valid
            if (quizDTO.quizTags.isNotEmpty()) {
                val validTagIds = validTags.mapNotNull { it.id }
                val invalidTagIds = quizDTO.quizTags.filter { it !in validTagIds }
                if (invalidTagIds.isNotEmpty()) {
                    throw NotFoundException("Invalid quiz tags for manager's semesters: $invalidTagIds")
                }
            }
            val quizTags = if (quizDTO.quizTags.isEmpty()) validTags else quizTagsRepository.findAllById(quizDTO.quizTags)
            


            val duration = quizDTO.durationInMinutes.toDuration(DurationUnit.MINUTES)
            val password = if(quizDTO.password != null && quizDTO.password.isNotEmpty()) quizDTO.password else null

            // Create quiz entity
            val quiz =
                    Quiz(
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
                            course = courses.toMutableList(),
                            batch = batches.toMutableList(),
                            student = students.toMutableList(),
                            lab = labs.toMutableList(),
                            password = password,
                    )

            // Create quiz user relationship
            val quizUser =
                    QuizUser(
                            id = QuizUserId(quiz.id, user.id),
                            quiz = quiz,
                            user = user,
                            tags = SharedTags.OWNER
                    )
            quiz.sharedUsers.add(quizUser)

            // Save quiz
            val savedQuiz = quizRepository.save(quiz)

            logger.info("Successfully created quiz with ID: {} for user: {}", savedQuiz.id, userId)
            return savedQuiz.id ?: throw QuizBusinessLogicException("Quiz ID is null after save")
        } catch (e: DataAccessException) {
            logger.error("Database error while creating quiz for user: {}", userId, e)
            throw QuizDatabaseException("create quiz", e)
        } catch (e: Exception) {
            logger.error("Unexpected error while creating quiz for user: {}", userId, e)
            throw e
        }
    }

    @Transactional
    fun editQuiz(dto: PatchQuizDTO, quizId: UUID, userId: String): QuizPreviewDTO {
        logger.info("Editing quiz: {} by user: {}", quizId, userId)

        try {
            val existingQuiz = quizRepository.findById(quizId).orElseThrow {
                NotFoundException("Quiz with id $quizId not found")
            }
            
            // Validate that quiz is not published
            QuizValidationUtils.validateQuizNotPublished(existingQuiz)
            // Validate input
            QuizValidationUtils.validatePatchQuizData(dto)

            // Fetch and validate quiz


            // Check permissions
            //QuizSecurityUtils.ensureOwnership(existingQuiz, userId)
            QuizSecurityUtils.validateQuizState(existingQuiz, "EDIT")

            // QuizTags validation for edit
            val semestersManaged = semesterRepository.findByManagerId(listOf(userRepository.findById(userId).orElseThrow { NotFoundException("User with ID $userId not found") }))
            val validTags = semestersManaged.flatMap { it.quizTags }.distinct()
            if (dto.quizTags != null && dto.quizTags.isNotEmpty()) {
                val validTagIds = validTags.mapNotNull { it.id }
                val invalidTagIds = dto.quizTags.filter { it !in validTagIds }
                if (invalidTagIds.isNotEmpty()) {
                    throw NotFoundException("Invalid quiz tags for manager's semesters: $invalidTagIds")
                }
            }
            val quizTags = if (dto.quizTags == null || dto.quizTags.isEmpty()) validTags else quizTagsRepository.findAllById(dto.quizTags)

            // Create updated quiz
            val updatedQuiz = patchQuiz(existingQuiz, dto, quizTags)
            val savedQuiz = quizRepository.save(updatedQuiz)
            val status: QuizStatus = when{
                savedQuiz.startTime.isAfter(Instant.now()) -> QuizStatus.UPCOMING
                savedQuiz.endTime.isBefore(Instant.now()) -> QuizStatus.COMPLETED
                else -> QuizStatus.ACTIVE
            }

            logger.info("Successfully updated quiz: {} by user: {}", quizId, userId)
            return QuizPreviewDTO(
                id = savedQuiz.id,
                name = savedQuiz.name,
                description = savedQuiz.description ?: "",
                startTime = savedQuiz.startTime,
                endTime = savedQuiz.endTime,
                batches = savedQuiz.batch.map { batch -> batch.name },
                labs = savedQuiz.lab.map { lab -> lab.name },
                duration = savedQuiz.duration,
                publishResult = savedQuiz.publishResult,
                status = status,
                isProtected = savedQuiz.password != null || savedQuiz.password?.isNotEmpty() == true,
                courseCodes = savedQuiz.course.map{
                        course -> CourseInfoDTO(
                    id = course.id,
                    name = course.name,
                    courseCode = course.code
                )
                },
                isPublished = savedQuiz.publishQuiz
            )
        } catch (e: DataAccessException) {
            logger.error("Database error while editing quiz: {} by user: {}", quizId, userId, e)
            throw QuizDatabaseException("edit quiz", e)
        } catch (e: Exception) {
            logger.error("Error while editing quiz: {} by user: {}", quizId, userId, e)
            throw e
        }
    }

    @Transactional
    fun deleteQuiz(quizId: UUID, userId: String) {
        logger.info("Deleting quiz: {} by user: {}", quizId, userId)

        try {
            val quiz =
                    quizRepository.findById(quizId).orElseThrow {
                        QuizNotFoundException(quizId.toString())
                    }

            // Check permissions
            QuizSecurityUtils.ensureOwnership(quiz, userId)
            QuizSecurityUtils.validateQuizState(quiz, "DELETE")

            quizRepository.deleteById(quizId)

            logger.info("Successfully deleted quiz: {} by user: {}", quizId, userId)
        } catch (e: DataAccessException) {
            logger.error("Database error while deleting quiz: {} by user: {}", quizId, userId, e)
            throw QuizDatabaseException("delete quiz", e)
        } catch (e: Exception) {
            logger.error("Error while deleting quiz: {} by user: {}", quizId, userId, e)
            throw e
        }
    }

    @Transactional(readOnly = true)
    fun getQuizById(quizId: UUID, userId: String): QuizPreviewDTO {
        logger.info("Fetching quiz: {} by user: {}", quizId, userId)

        try {
            val quiz =
                    quizRepository.findById(quizId).orElseThrow {
                        QuizNotFoundException(quizId.toString())
                    }
            // Check permissions
            // QuizSecurityUtils.ensureQuizAccess(quiz, userId)
            val status: QuizStatus = when{
                quiz.startTime.isAfter(Instant.now()) -> QuizStatus.UPCOMING
                quiz.endTime.isBefore(Instant.now()) -> QuizStatus.COMPLETED
                else -> QuizStatus.ACTIVE
            }

            val result = QuizPreviewDTO(
                id = quiz.id,
                name = quiz.name,
                description = quiz.description ?: "",
                startTime = quiz.startTime,
                endTime = quiz.endTime,
                batches = quiz.batch.map { batch -> batch.name },
                labs = quiz.lab.map { lab -> lab.name },
                duration = quiz.duration,
                publishResult = quiz.publishResult,
                status = status,
                isProtected = quiz.password != null || quiz.password?.isNotEmpty() == true,
                courseCodes = quiz.course.map{
                        course -> CourseInfoDTO(
                    id = course.id,
                    name = course.name,
                    courseCode = course.code
                )
                },
                isPublished = quiz.publishQuiz
            )
            
            logger.debug("Successfully retrieved quiz: {} for user: {}", quizId, userId)
            return result
        } catch (e: DataAccessException) {
            logger.error("Database error while fetching quiz: {} by user: {}", quizId, userId, e)
            throw QuizDatabaseException("fetch quiz", e)
        } catch (e: Exception) {
            logger.error("Error while fetching quiz: {} by user: {}", quizId, userId, e)
            throw e
        }
    }

    private fun patchQuiz(existing: Quiz, dto: PatchQuizDTO, quizTags: List<QuizTags>): Quiz {
        logger.debug("Applying patch to quiz: {}", existing.id)

        try {
            val updatedCourses =
                    dto.courseIds?.let {
                        val courses = courseRepository.findAllById(it)
                        if (courses.size != it.size) {
                            val foundIds = courses.map { course -> course.id }
                            val missingIds = it.filterNot { id -> foundIds.contains(id) }
                            throw NotFoundException("Courses not found: $missingIds")
                        }
                        courses.toMutableList()
                    }
                            ?: existing.course

            val updatedBatches =
                    dto.batchIds?.let {
                        val batches = batchRepository.findAllById(it)
                        if (batches.size != it.size) {
                            val foundIds = batches.map { batch -> batch.id }
                            val missingIds = it.filterNot { id -> foundIds.contains(id) }
                            throw NotFoundException("Batches not found: $missingIds")
                        }
                        batches.toMutableList()
                    }
                            ?: existing.batch

            val updatedLabs =
                    dto.labIds?.let {
                        val labs = labRepository.findAllById(it)
                        if (labs.size != it.size) {
                            val foundIds = labs.map { lab -> lab.id }
                            val missingIds = it.filterNot { id -> foundIds.contains(id) }
                            throw NotFoundException("Labs not found: $missingIds")
                        }
                        labs.toMutableList()
                    }
                            ?: existing.lab

            val updatedStudents =
                    dto.studentIds?.let {
                        val students = userRepository.findAllById(it)
                        if (students.size != it.size) {
                            val foundIds = students.map { student -> student.id }
                            val missingIds = it.filterNot { id -> foundIds.contains(id) }
                            throw NotFoundException("Students not found: $missingIds")
                        }
                        students.toMutableList()
                    }
                            ?: existing.student

            return Quiz(
                    id = existing.id,
                    name = dto.name ?: existing.name,
                    description = dto.description ?: existing.description,
                    instructions = dto.instructions ?: existing.instructions,
                    startTime = dto.startTime ?: existing.startTime,
                    endTime = dto.endTime ?: existing.endTime,
                    duration = dto.durationInMinutes?.toDuration(DurationUnit.MINUTES)
                                    ?: existing.duration,
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
                    sharedUsers = existing.sharedUsers,
                    password = dto.password ?: existing.password,
                    quizTags = existing.quizTags.toMutableList().apply { addAll(quizTags) }
            )
        } catch (e: Exception) {
            logger.error("Error while patching quiz: {}", existing.id, e)
            throw e
        }
    }



    @Transactional
    fun unpublishQuiz(quizId: UUID) {
        logger.info("Unpublishing quiz: {}", quizId)
        
        try {
            val quiz = quizRepository.findById(quizId).orElseThrow { 
                NotFoundException("Quiz with id $quizId not found") 
            }
            val student = quizStudentRepository.findByQuizId(quizId)

            // Validate that quiz can be unpublished
            QuizValidationUtils.validateQuizUnpublish(quiz,student)

            // Create unpublished version of quiz
            val unpublishedQuiz = Quiz(
                id = quiz.id,
                name = quiz.name,
                description = quiz.description,
                instructions = quiz.instructions,
                startTime = quiz.startTime,
                endTime = quiz.endTime,
                duration = quiz.duration,
                password = quiz.password,
                fullScreen = quiz.fullScreen,
                shuffleQuestions = quiz.shuffleQuestions,
                shuffleOptions = quiz.shuffleOptions,
                linearQuiz = quiz.linearQuiz,
                calculator = quiz.calculator,
                autoSubmit = quiz.autoSubmit,
                publishResult = quiz.publishResult,
                publishQuiz = false,
                section = quiz.section,
                course = quiz.course,
                student = quiz.student,
                lab = quiz.lab,
                batch = quiz.batch,
                createdAt = quiz.createdAt,
                noOfSets = quiz.noOfSets,
                sharedUsers = quiz.sharedUsers
            )

            quizRepository.save(unpublishedQuiz)
            logger.info("Successfully unpublished quiz: {}", quizId)
        } catch (e: Exception) {
            logger.error("Error while unpublishing quiz: {}", quizId, e)
            throw e
        }
    }

    fun publishQuiz(quizId: UUID, dto: SelectionCriteriaDTO?,noOfQuestions:Int?) {
        val quiz =
                quizRepository.findById(quizId).orElseThrow { NotFoundException("Quiz not found") }

        // Validate quiz assignments using QuizValidationUtils
        QuizValidationUtils.validateQuizAssignments(quiz)

        if (dto != null) {
        val allQuestions: List<QuizQuestion> = quiz.section.flatMap { it.quizQuestions }

        val grouped: Map<UUID, Map<String, List<QuizQuestion>>> =
                allQuestions
                        .flatMap { quizQuestion ->
                            quizQuestion.question.topic.map { topic -> topic.id!! to quizQuestion }
                        }
                        .groupBy({ it.first }, { it.second })
                        .mapValues { (_, quizQuestions) ->
                            quizQuestions.groupBy { it.question.difficulty.name.lowercase() }
                        }

        val topicDifficultyCombos = mutableListOf<List<List<QuizQuestion>>>()

        for (topic in dto.criteria) {
            val topicGroup =
                    grouped[topic.topicId]
                            ?: throw IllegalStateException(
                                    "No questions found for topic ${topic.topicId}"
                            )

            fun getCombinations(diff: String, count: Int): List<List<QuizQuestion>> {
                val qList = topicGroup[diff] ?: emptyList()
                if (qList.size < count)
                        throw IllegalStateException(
                                "Insufficient questions for $diff in topic ${topic.topicId}"
                        )
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

        val validSets =
                allCombinations
                        .map { set -> set.flatten() }
                        .filter { quizQuestions ->
                            quizQuestions.sumOf { it.question.marks } == dto.totalMarks
                        }
                        .distinctBy { it.mapNotNull { q -> q.question.id }.sorted() }

        if (validSets.size < dto.noSets){
                throw IllegalStateException(
                        "Only ${validSets.size} valid sets available, but ${dto.noSets} requested"
                )}

            for (i in 1 until dto.noSets) {
                val set = validSets[i]

                val quizSet = QuizSet(setNumber = i, quiz = quiz)

                val quizSetQuestions =
                    set.mapIndexed { order, quizQuestion ->
                        QuizSetQuestion(quizSet = quizSet, question = quizQuestion, )
                    }

                quizSet.questions.addAll(quizSetQuestions)
                quizSetRepository.save(quizSet)
            }

        val publishedQuiz = quiz.publishQuiz(dto.noSets)
        quizRepository.save(publishedQuiz)


    }
        else{
            val allQuestions: List<QuizQuestion> = quiz.section.flatMap { it.quizQuestions }.shuffled()
            val selectedQuestions = if(noOfQuestions != null) allQuestions.take(noOfQuestions) else allQuestions
            val quizSet = QuizSet(setNumber = 1, quiz = quiz)
            val quizSetQuestions = selectedQuestions.mapIndexed { order, quizQuestion ->
                QuizSetQuestion(quizSet = quizSet, question = quizQuestion, )
            }
            quizSet.questions.addAll(quizSetQuestions)
            quizSetRepository.save(quizSet)
            val publishedQuiz = quiz.publishQuiz(1)
            quizRepository.save(publishedQuiz)





        }}

    fun addStudentToQuiz(quizId: UUID, studentId: List<String>) {
        val quiz =
                quizRepository.findById(quizId).orElseThrow {
                    NotFoundException("quiz with id $quizId not found")
                }
        val students = userRepository.findAllById(studentId).filter { student -> student !in quiz.student }
        quiz.student.addAll(students)
        quizRepository.save(quiz)
    }

    fun removeStudentFromQuiz(quizId: UUID, studentId: List<String>) {
        val quiz =
                quizRepository.findById(quizId).orElseThrow {
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

    @Transactional
    fun shareQuiz(quizID: UUID, shareDTO: ShareQuizDTO) {
        logger.info("Processing share request for quiz: {} with users: {}", quizID, shareDTO.userID)
        
        val quiz = quizRepository.findById(quizID).orElseThrow {
            NotFoundException("Quiz with id $quizID not found")
        }

        val users = userRepository.findAllById(shareDTO.userID)
        if (users.isEmpty()) {
            logger.warn("No valid users found to share quiz with")
            //throw ValidationException("No valid users found", "userID")
        }

        // Create shared users in a transaction
        users.forEach { user ->
            // Check if sharing already exists
            val existingShare = quiz.sharedUsers.find { it.user?.id == user.id }
            if (existingShare == null) {
                val quizUser = QuizUser(
                    id = QuizUserId(quizID, user.id),
                    quiz = quiz,
                    user = user,
                    tags = SharedTags.SHARED
                )
                quiz.sharedUsers.add(quizUser)
            }
        }
        
        // Save all changes in one transaction
        try {
            quizRepository.save(quiz)
            logger.info("Successfully shared quiz {} with {} users", quizID, users.size)
        } catch (e: Exception) {
            logger.error("Error saving shared quiz state: {}", e.message, e)
            throw QuizDatabaseException("Error saving shared quiz state", e)
        }
    }

    fun checkAvailability(quizId: UUID, dto: SelectionCriteriaDTO): PermutationsDTO {
        val quiz =
                quizRepository.findById(quizId).orElseThrow {
                    NotFoundException("Quiz with id $quizId not found")
                }

        val allQuestions: List<BaseQuestion> =
                quiz.section.flatMap { it.quizQuestions }.mapNotNull { it.question }

        val grouped: Map<UUID, Map<String, List<BaseQuestion>>> =
                allQuestions
                        .flatMap { question ->
                            question.topic.map { topic -> topic.id!! to question }
                        }
                        .groupBy({ it.first }, { it.second })
                        .mapValues { (_, questions) ->
                            questions.groupBy { it.difficulty.name.lowercase() }
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

        val allCombinations = CombinationUtils.cartesianProduct(topicDifficultyCombos)

        val validSets =
                allCombinations
                        .filter { set -> set.flatten().sumOf { it.marks } == dto.totalMarks }
                        .map { set -> set.flatten().mapNotNull { it.id }.sorted() }
                        .toSet()

        val totalPerms =
                validSets.sumOf { ids ->
                    val freq = ids.groupingBy { it }.eachCount()
                    val numerator = CombinationUtils.factorial(ids.size)
                    val denominator =
                            freq.values.fold(1L) { acc, count ->
                                acc * CombinationUtils.factorial(count)
                            }
                    numerator / denominator
                }

        return PermutationsDTO(validSets.isNotEmpty(), totalPerms.toInt())
    }

    // returns a count of total, live, upcoming and completed for all quizzes
    fun getQuizCounts(): Map<String, Long> {
        val now = Instant.now() // Use Instant instead of Date
        val totalCount = quizRepository.count()
        val liveCount = quizRepository.countByStartTimeBeforeAndEndTimeAfter(
            startTime = now,
            endTime = now
        )
        val upcomingCount = quizRepository.countByStartTimeAfter(now)
        val completedCount = quizRepository.countByEndTimeBefore(now)

        return mapOf(
            "total" to totalCount,
            "live" to liveCount,
            "upcoming" to upcomingCount,
            "completed" to completedCount
        )
    }

    fun getQuizQuestions(quizId: UUID, userId: String): List<QuizQuestionsReturnDTO> {
        logger.info("Fetching quiz questions for quiz: {} by user: {}", quizId, userId)

        val quiz = quizRepository.findById(quizId).orElseThrow { QuizNotFoundException(quizId.toString())
        }
        val quizQuestions = quiz.section.flatMap { it.quizQuestions }
        val result = quizQuestions.map{
            quizQuestion ->
            val baseQuestion = quizQuestion.question
            val finalQuestion = baseQuestion.mapToBankType(quizQuestion.id)
            QuizQuestionsReturnDTO(
                question = finalQuestion,
                sectionId = quizQuestion.section.id

            )
   }
        return result
    }

//    fun getQuizQuestionsByTopic(
//        topicIds: List<UUID>?,
//        quizId: UUID,
//        userId: String
//    ): List<BankQuestionsReturnDTO> {
//        logger.info(
//            "Fetching questions by topics for quizId: {} by user: {}",
//            quizId,
//            userId
//        )
//
//       val quiz = quizRepository.findById(quizId).orElseThrow { QuizNotFoundException(quizId.toString()) }
//
//        val quizQuestions = quiz.section.flatMap { it.quizQuestions }
//
//
//
//        val result = if (topicIds.isNullOrEmpty()) {
//            quizQuestions.filter { quizQuestion ->
//                quizQuestion.question.topic.isEmpty()
//            }
//        } else {
//            val topics = topicRepo.findAllById(topicIds)
//            quizQuestions.filter { bankQuestion ->
//                bankQuestion.question.topic.any { it in topics }
//            }
//        }
//        val finalResult = result.map { it.question.mapToBankType(it.id) }
//
//        logger.debug("Retrieved {} questions by topics for quiz: {}", finalResult.size, quizId)
//        return finalResult
//    }

    fun getQuizQuestionsBySection(sectionId: UUID, quizId: UUID, userId: String):List<BankQuestionsReturnDTO> {
        logger.info(
            "Fetching questions by section for quizId: {} by user: {}",
            quizId,
            userId
        )
        val section = sectionRepository.findById(sectionId)
        val quizQuestions = section.map { it.quizQuestions }.orElseThrow { QuizNotFoundException(quizId.toString()) }
        logger.debug("Retrieved {} questions by section for quiz: {}", quizQuestions.size, quizId)
        val result = quizQuestions.map{
                quizQuestion ->
            val baseQuestion = quizQuestion.question
            baseQuestion.mapToBankType(quizQuestion.id)
        }
        return result

    }

    fun getQuizQuestionById(questionId : UUID) : BankQuestionsReturnDTO
    {
        val quizQuestion = quizQuestionRepository.findById(questionId).orElseThrow{ QuizNotFoundException(questionId.toString())}
        val baseQuestion =quizQuestion.question.mapToBankType(questionId)

        return baseQuestion

    }

    fun unshareBank(quizId: UUID, dto: ShareQuizDTO, userId: String) {
        logger.info(
            "Unsharing quiz: {} from users: {} by user: {}",
            quizId,
            dto.userID,
            userId
        )

        val quiz =
            quizRepository.findById(quizId).orElseThrow {
                QuizNotFoundException(quizId.toString())
            }

        QuizSecurityUtils.ensureOwnership(quiz, userId)

        var unsharedCount = 0
        dto.userID.forEach { userIdToUnshare ->
            val removed =
                quiz.sharedUsers.removeIf {
                    it.user?.id == userIdToUnshare &&
                            it.tags == SharedTags.SHARED
                }
            if (removed) unsharedCount++
        }

        quizRepository.save(quiz)
        logger.info("Successfully unshared bank: {} from {} users",quizId, unsharedCount)
    }

    fun getSharedQuizzes(userId: String) : List<QuizPreviewDTO>{
        logger.info("Fetching shared quizzes for user: {}", userId)
        val sharedQuizzes = quizRepository.getSharedQuizzes(userId)
        val final = sharedQuizzes.map {
            quiz ->
            val status: QuizStatus = when{
                quiz.startTime.isAfter(Instant.now()) -> QuizStatus.UPCOMING
                quiz.endTime.isBefore(Instant.now()) -> QuizStatus.COMPLETED
                else -> QuizStatus.ACTIVE
            }

            QuizPreviewDTO(
                id = quiz.id,
                name = quiz.name,
                description = quiz.description ?: "",
                startTime = quiz.startTime,
                endTime = quiz.endTime,
                batches = quiz.batch.map { batch -> batch.name },
                labs = quiz.lab.map { lab -> lab.name },
                duration = quiz.duration,
                publishResult = quiz.publishResult,
                status = status,
                isProtected = quiz.password != null || quiz.password?.isNotEmpty() == true,
                courseCodes = quiz.course.map{
                        course -> CourseInfoDTO(
                    id = course.id,
                    name = course.name,
                    courseCode = course.code
                )
                },
                isPublished = quiz.publishQuiz
            )

        }
        return final
    }

    fun sharedQuizzes(userId: String): List<SharedQuizPreviewDTO> {
        logger.info("Fetching shared quizzes for user: {}", userId)
        val sharedQuizzes = quizRepository.findByOwnerId(userId)

        return sharedQuizzes.mapNotNull { quiz ->
            if (quiz.sharedUsers.size > 1) {
                val sharedUsers = quiz.sharedUsers.filter { it.tags == SharedTags.SHARED }
                if (sharedUsers.isNotEmpty()) {
                    val userIds = sharedUsers.mapNotNull { it.user?.id }
                    val status: QuizStatus = when{
                        quiz.startTime.isAfter(Instant.now()) -> QuizStatus.UPCOMING
                        quiz.endTime.isBefore(Instant.now()) -> QuizStatus.COMPLETED
                        else -> QuizStatus.ACTIVE
                    }
                    SharedQuizPreviewDTO(
                        id = quiz.id,
                        name = quiz.name,
                        status = status,
                        isProtected = quiz.password != null || quiz.password?.isNotEmpty() == true,
                        courseCodes = quiz.course.map { course -> course.code },
                        sharedWith = userIds
                    )
                } else null
            } else null
        }

    }

    fun getSharedUsers(quizId: UUID) : List<SharedUserDTO>{
        val quiz = quizRepository.findById(quizId).orElseThrow{ QuizNotFoundException(quizId.toString())}
        val final = quiz.sharedUsers.map { users ->
            val user = SimpleUserDTO(
                id = users.user?.id,
                name = users.user?.name,
                email = users.user?.email,
                profileId = users.user?.profileId
            )
            SharedUserDTO(
                user = user,
                tag = users.tags
            )
        }
        return final
    }
}


