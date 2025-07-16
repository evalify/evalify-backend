package com.evalify.evalifybackend.quiz.service
import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizPreviewDTO
import com.evalify.evalifybackend.quiz.domain.DTO.student.PreviewQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.student.QuizStatusDTO
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class PreviewQuizStudentService(
    private val courseRepository: CourseRepository,
    private val batchRepository: BatchRepository,
    private val quizStudentRepository: QuizStudentRepository,
    private val quizRepository: QuizRepository, private val userRepository: UserRepository) {

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
            if(quiz.publishQuiz){
            val completedQuiz = quizStudentRepository.findByQuizIdAndStudentId(quizId = quiz.id, userId = studentId)
            if (quiz.status == QuizStatus.COMPLETED && completedQuiz == null) {
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
                    status = QuizStatusDTO.from(quiz.status.toString())
                )
                filteredQuiz?.add(previewQuizDTO)

            }
        }
        }
        return if (quizStatus == null) filteredQuiz else filteredQuiz?.filter { quiz -> quiz.status == quizStatus }

    }
}