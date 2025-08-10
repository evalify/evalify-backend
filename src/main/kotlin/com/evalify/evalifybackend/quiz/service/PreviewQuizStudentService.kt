package com.evalify.evalifybackend.quiz.service
import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.student.PreviewQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.student.QuizStatusDTO
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class PreviewQuizStudentService(
    private val courseRepository: CourseRepository,
    private val batchRepository: BatchRepository,
    private val quizStudentRepository: QuizStudentRepository,
    private val quizRepository: QuizRepository, private val userRepository: UserRepository) {

    fun getQuizByCourse(courseId: UUID,studentId: String): List<PreviewQuizDTO> {
        val course = courseRepository.findById(courseId).orElseThrow { NotFoundException("course with id $courseId not found") }
        println(course)
        val  courseQuizzes:MutableList<PreviewQuizDTO> = mutableListOf()
        course.quiz.forEach { quiz ->
            if(quiz.publishQuiz == true){
            val completedQuiz = quizStudentRepository.findByQuizIdAndStudentId(quizId = quiz.id, userId = studentId)
            val status: QuizStatus = when{
                quiz.startTime.isAfter(Instant.now()) -> QuizStatus.UPCOMING
                quiz.endTime.isBefore(Instant.now()) -> QuizStatus.COMPLETED
                else -> QuizStatus.ACTIVE
            }
            if (status == QuizStatus.COMPLETED && completedQuiz == null) {
                val previewQuizDTO = PreviewQuizDTO(
                    id = quiz.id,
                    quizTags = quiz.quizTags.map { quizTags ->
                        QuizTagsReturnDTO(
                            quizTags.id,
                            quizTags.name,
                            quizTags.description
                        )
                    },
                    instructions = quiz.instructions,
                    linearQuiz = quiz.linearQuiz,
                    protected = quiz.password != null,
                    name = quiz.name,
                    description = quiz.description,
                    startTime = quiz.startTime,
                    endTime = quiz.endTime,
                    duration = quiz.duration,
                    status = QuizStatusDTO.MISSED
                )
                courseQuizzes.add(previewQuizDTO)

            } else {
                val status: QuizStatus = when{
                    quiz.startTime.isAfter(Instant.now()) -> QuizStatus.UPCOMING
                    quiz.endTime.isBefore(Instant.now()) -> QuizStatus.COMPLETED
                    else -> QuizStatus.ACTIVE
                }
                val previewQuizDTO = PreviewQuizDTO(
                    id = quiz.id,
                    quizTags = quiz.quizTags.map { quizTags ->
                        QuizTagsReturnDTO(
                            quizTags.id,
                            quizTags.name,
                            quizTags.description
                        )
                    },
                    instructions = quiz.instructions,
                    linearQuiz = quiz.linearQuiz,
                    protected = quiz.password != null,
                    name = quiz.name,
                    description = quiz.description,
                    startTime = quiz.startTime,
                    endTime = quiz.endTime,
                    duration = quiz.duration,
                    status = QuizStatusDTO.from(status.toString())
                )
                courseQuizzes.add(previewQuizDTO)

            }
         }
        }
        return courseQuizzes
    }

    fun getStudentQuiz(studentId: String,quizStatus: QuizStatusDTO?): List<PreviewQuizDTO>? {

        val student: User = userRepository.findById(studentId).orElseThrow {
            NotFoundException("Student $studentId Not Found")
        }

        //check with student id
        val studentQuiz: List<Quiz>? = quizRepository.findByStudentId(studentId)

        //check with course
        val courseQuizzes: List<Quiz>? = courseRepository.findCourseByStudent(student).flatMap { course -> course.quiz }

        //check with batches
        val batchQuizzes: List<Quiz>? = batchRepository.findBatchByStudent(student)?.quiz?.toList()

        val allQuizzes: List<Quiz> =
            ((studentQuiz ?: emptyList()) +
                    (courseQuizzes ?: emptyList()) +
                    (batchQuizzes ?: emptyList())).distinctBy { it.id }

        val filteredQuiz: MutableList<PreviewQuizDTO>? = mutableListOf()

        allQuizzes.forEach { quiz ->
            if(quiz.publishQuiz == true){
            val completedQuiz = quizStudentRepository.findByQuizIdAndStudentId(quizId = quiz.id, userId = studentId)
                val status: QuizStatus = when{
                    quiz.startTime.isAfter(Instant.now()) -> QuizStatus.UPCOMING
                    quiz.endTime.isBefore(Instant.now()) -> QuizStatus.COMPLETED
                    else -> QuizStatus.ACTIVE
                }
            if (status == QuizStatus.COMPLETED && completedQuiz == null) {
                    val previewQuizDTO = PreviewQuizDTO(
                        id = quiz.id,
                        quizTags = quiz.quizTags.map { quizTags ->
                            QuizTagsReturnDTO(
                                quizTags.id,
                                quizTags.name,
                                quizTags.description
                            )
                        },
                        instructions = quiz.instructions,
                        linearQuiz = quiz.linearQuiz,
                        protected = quiz.password != null,
                        name = quiz.name,
                        description = quiz.description,
                        startTime = quiz.startTime,
                        endTime = quiz.endTime,
                        duration = quiz.duration,
                        status = QuizStatusDTO.MISSED
                    )
                    filteredQuiz?.add(previewQuizDTO)

            } else {
                val status: QuizStatus = when{
                    quiz.startTime.isAfter(Instant.now()) -> QuizStatus.UPCOMING
                    quiz.endTime.isBefore(Instant.now()) -> QuizStatus.COMPLETED
                    else -> QuizStatus.ACTIVE
                }
                val previewQuizDTO = PreviewQuizDTO(
                    id = quiz.id,
                    quizTags = quiz.quizTags.map { quizTags ->
                        QuizTagsReturnDTO(
                            quizTags.id,
                            quizTags.name,
                            quizTags.description
                        )
                    },
                    instructions = quiz.instructions,
                    linearQuiz = quiz.linearQuiz,
                    protected = quiz.password != null,
                    name = quiz.name,
                    description = quiz.description,
                    startTime = quiz.startTime,
                    endTime = quiz.endTime,
                    duration = quiz.duration,
                    status = QuizStatusDTO.from(status.toString())
                )
                filteredQuiz?.add(previewQuizDTO)

            }
        }
        }
        return if (quizStatus == null) filteredQuiz else filteredQuiz?.filter { quiz -> quiz.status == quizStatus }

    }
}