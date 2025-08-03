package com.evalify.evalifybackend.course.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.semester.domain.Semester
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.*
import java.util.UUID

enum class CourseType {
    CORE,
    ELECTIVE,
    MICRO_CREDENTIAL
}

@Entity
@Table(name = "course")
 class Course (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val name: String,
    @Column(columnDefinition = "TEXT")
    val description: String,
    var code: String = "",
    val image: String? = null,
    @Enumerated(EnumType.STRING)
    val type: CourseType,

    @ManyToOne(fetch = FetchType.LAZY)
    val semester: Semester,

    @ManyToMany
    @JoinTable(
        name = "course_student",
        joinColumns = [JoinColumn(name="course_id")],
        inverseJoinColumns = [JoinColumn(name="student_id")]
    )
    val students: MutableList<User> = mutableListOf(),
    @ManyToMany
    @JoinTable(
        name = "course_instructor",
        joinColumns = [JoinColumn(name="course_id")],
        inverseJoinColumns = [JoinColumn(name="instructor_id")]
    )
    val instructors: MutableList<User> = mutableListOf(),
    @ManyToMany
    @JoinTable(
        name = "course_batch",
        joinColumns = [JoinColumn(name="course_id")],
        inverseJoinColumns = [JoinColumn(name="batch_id")]
    )
    val batches: MutableList<Batch> = mutableListOf(),

    @ManyToMany(mappedBy = "course")
     val quiz: List<Quiz> = listOf(),

    @ManyToMany
    @JoinTable(
        name = "course_bank",
        joinColumns = [JoinColumn(name="course_id")],
        inverseJoinColumns = [JoinColumn(name="bank_id")]
    )
     val bank:MutableList<Bank> = mutableListOf()
)

