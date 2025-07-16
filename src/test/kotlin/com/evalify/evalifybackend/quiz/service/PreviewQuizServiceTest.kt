package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.course.domain.CourseType
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.evalify.evalifybackend.quiz.domain.QuizStudent
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.semester.domain.Semester
import com.evalify.evalifybackend.user.domain.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.hours

class PreviewQuizServiceTest {

    @Mock
    private lateinit var quizRepository: QuizRepository

    @Mock
    private lateinit var quizStudentRepository: QuizStudentRepository

    private lateinit var previewQuizService: PreviewQuizService

    private val studentId = "student123"
    private val quizId = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        previewQuizService = PreviewQuizService(quizRepository, quizStudentRepository)
    }

    @Test
    fun `should return published quizzes for student`() {
        // Given
        val course = createMockCourse("CS101")
        val user = createMockUser(studentId)
        val quiz = createMockQuiz(
            id = quizId,
            published = true,
            startTime = Instant.now().minusSeconds(3600), // 1 hour ago
            endTime = Instant.now().plusSeconds(3600),    // 1 hour from now
            course = course,
            student = user
        )

        `when`(quizRepository.findPublishedQuizzesByStudentId(studentId)).thenReturn(listOf(quiz))
        `when`(quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId)).thenReturn(null)

        // When
        val result = previewQuizService.getPreviewQuiz(studentId)

        // Then
        assertEquals(1, result.size)
        val previewDto = result[0]
        assertEquals(quiz.id, previewDto.id)
        assertEquals(quiz.name, previewDto.name)
        assertEquals(QuizStatus.ACTIVE, previewDto.status)
        assertTrue(previewDto.canStart)
        assertFalse(previewDto.isSubmitted)
        assertEquals(listOf("CS101"), previewDto.courseCodes)
    }

    @Test
    fun `should return upcoming quiz status`() {
        // Given
        val course = createMockCourse("CS102")
        val user = createMockUser(studentId)
        val quiz = createMockQuiz(
            id = quizId,
            published = true,
            startTime = Instant.now().plusSeconds(3600), // 1 hour from now
            endTime = Instant.now().plusSeconds(7200),   // 2 hours from now
            course = course,
            student = user
        )

        `when`(quizRepository.findPublishedQuizzesByStudentId(studentId)).thenReturn(listOf(quiz))

        // When
        val result = previewQuizService.getPreviewQuiz(studentId)

        // Then
        assertEquals(1, result.size)
        val previewDto = result[0]
        assertEquals(QuizStatus.UPCOMING, previewDto.status)
        assertFalse(previewDto.canStart)
    }

    @Test
    fun `should return ended quiz status`() {
        // Given
        val course = createMockCourse("CS103")
        val user = createMockUser(studentId)
        val quiz = createMockQuiz(
            id = quizId,
            published = true,
            startTime = Instant.now().minusSeconds(7200), // 2 hours ago
            endTime = Instant.now().minusSeconds(3600),   // 1 hour ago
            course = course,
            student = user
        )

        `when`(quizRepository.findPublishedQuizzesByStudentId(studentId)).thenReturn(listOf(quiz))

        // When
        val result = previewQuizService.getPreviewQuiz(studentId)

        // Then
        assertEquals(1, result.size)
        val previewDto = result[0]
        assertEquals(QuizStatus.ENDED, previewDto.status)
        assertFalse(previewDto.canStart)
    }

    @Test
    fun `should filter quizzes by status`() {
        // Given
        val course = createMockCourse("CS104")
        val user = createMockUser(studentId)
        val activeQuiz = createMockQuiz(
            id = UUID.randomUUID(),
            published = true,
            startTime = Instant.now().minusSeconds(3600),
            endTime = Instant.now().plusSeconds(3600),
            course = course,
            student = user,
            name = "Active Quiz"
        )
        val upcomingQuiz = createMockQuiz(
            id = UUID.randomUUID(),
            published = true,
            startTime = Instant.now().plusSeconds(3600),
            endTime = Instant.now().plusSeconds(7200),
            course = course,
            student = user,
            name = "Upcoming Quiz"
        )

        `when`(quizRepository.findPublishedQuizzesByStudentId(studentId))
            .thenReturn(listOf(activeQuiz, upcomingQuiz))

        // When
        val activeQuizzes = previewQuizService.getPreviewQuizByStatus(studentId, QuizStatus.ACTIVE)
        val upcomingQuizzes = previewQuizService.getPreviewQuizByStatus(studentId, QuizStatus.UPCOMING)

        // Then
        assertEquals(1, activeQuizzes.size)
        assertEquals("Active Quiz", activeQuizzes[0].name)
        assertEquals(QuizStatus.ACTIVE, activeQuizzes[0].status)

        assertEquals(1, upcomingQuizzes.size)
        assertEquals("Upcoming Quiz", upcomingQuizzes[0].name)
        assertEquals(QuizStatus.UPCOMING, upcomingQuizzes[0].status)
    }

    @Test
    fun `should return null for quiz not accessible to student`() {
        // Given
        `when`(quizRepository.findById(quizId)).thenReturn(Optional.empty())

        // When
        val result = previewQuizService.getPreviewQuizById(quizId, studentId)

        // Then
        assertNull(result)
    }

    @Test
    fun `should return quizzes with assignment types`() {
        // Given
        val course = createMockCourse("CS101")
        val batch = createMockBatch("Batch A")
        val user = createMockUser(studentId)
        
        val directQuiz = createMockQuiz(
            id = UUID.randomUUID(),
            published = true,
            startTime = Instant.now().minusSeconds(3600),
            endTime = Instant.now().plusSeconds(3600),
            course = course,
            student = user,
            batch = batch,
            name = "Direct Quiz"
        )

        `when`(quizRepository.findPublishedQuizzesByDirectAssignment(studentId))
            .thenReturn(listOf(directQuiz))
        `when`(quizRepository.findPublishedQuizzesByStudentCourses(studentId))
            .thenReturn(listOf(directQuiz))
        `when`(quizRepository.findPublishedQuizzesByStudentBatches(studentId))
            .thenReturn(emptyList())

        // When
        val result = previewQuizService.getPreviewQuizWithAssignmentTypes(studentId)

        // Then
        assertEquals(1, result.size)
        val quizWithAssignment = result[0]
        assertTrue(quizWithAssignment.assignmentTypes.contains(QuizAssignmentType.DIRECT_ASSIGNMENT))
        assertTrue(quizWithAssignment.assignmentTypes.contains(QuizAssignmentType.COURSE_ASSIGNMENT))
        assertFalse(quizWithAssignment.assignmentTypes.contains(QuizAssignmentType.BATCH_ASSIGNMENT))
    }

    @Test
    fun `should return detailed preview with batch information`() {
        // Given
        val course = createMockCourse("CS101")
        val batch = createMockBatch("Batch A")
        val user = createMockUser(studentId)
        
        val quiz = createMockQuiz(
            id = quizId,
            published = true,
            startTime = Instant.now().minusSeconds(3600),
            endTime = Instant.now().plusSeconds(3600),
            course = course,
            student = user,
            batch = batch,
            name = "Detailed Quiz"
        )

        `when`(quizRepository.findPublishedQuizzesByDirectAssignment(studentId))
            .thenReturn(listOf(quiz))
        `when`(quizRepository.findPublishedQuizzesByStudentCourses(studentId))
            .thenReturn(emptyList())
        `when`(quizRepository.findPublishedQuizzesByStudentBatches(studentId))
            .thenReturn(listOf(quiz))
        `when`(quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId))
            .thenReturn(null)

        // When
        val result = previewQuizService.getDetailedPreviewQuiz(studentId)

        // Then
        assertEquals(1, result.size)
        val detailedQuiz = result[0]
        assertEquals("Detailed Quiz", detailedQuiz.name)
        assertEquals(listOf("CS101"), detailedQuiz.courseCodes)
        assertEquals(listOf("Batch A"), detailedQuiz.batchNames)
        assertTrue(detailedQuiz.assignmentTypes.contains(QuizAssignmentType.DIRECT_ASSIGNMENT))
        assertTrue(detailedQuiz.assignmentTypes.contains(QuizAssignmentType.BATCH_ASSIGNMENT))
    }

    private fun createMockCourse(code: String): Course {
        val semester = mock(Semester::class.java)
        return Course(
            id = UUID.randomUUID(),
            name = "Test Course",
            description = "Test Description",
            code = code,
            type = CourseType.CORE,
            semester = semester
        )
    }

    private fun createMockUser(id: String): User {
        return mock(User::class.java).apply {
            `when`(this.id).thenReturn(id)
        }
    }

    private fun createMockQuiz(
        id: UUID,
        published: Boolean,
        startTime: Instant,
        endTime: Instant,
        course: Course,
        student: User,
        name: String = "Test Quiz"
    ): Quiz {
        return Quiz(
            id = id,
            name = name,
            description = "Test quiz description",
            instructions = "Test instructions",
            startTime = startTime,
            endTime = endTime,
            duration = 2.hours,
            publishQuiz = published,
            course = mutableListOf(course),
            student = mutableListOf(student)
        )
    }

    private fun createMockQuiz(
        id: UUID,
        published: Boolean,
        startTime: Instant,
        endTime: Instant,
        course: Course,
        student: User,
        batch: Batch,
        name: String = "Test Quiz"
    ): Quiz {
        return Quiz(
            id = id,
            name = name,
            description = "Test quiz description",
            instructions = "Test instructions",
            startTime = startTime,
            endTime = endTime,
            duration = 2.hours,
            publishQuiz = published,
            course = mutableListOf(course),
            student = mutableListOf(student),
            batch = mutableListOf(batch)
        )
    }

    private fun createMockBatch(name: String): Batch {
        return mock(Batch::class.java).apply {
            `when`(this.name).thenReturn(name)
        }
    }
}
