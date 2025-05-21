package com.evalify.evalifybackend.course.domain

import com.evalify.evalifybackend.batch.domain.Batch
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
    val description: String,
    val image: String? = null,
    val type: CourseType,

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val semester: Semester,
    @OneToMany(fetch = FetchType.LAZY)
    val student: MutableList<User> = mutableListOf(),
    @OneToMany(fetch = FetchType.LAZY)
    val instructors: MutableList<User> = mutableListOf(),
    @OneToMany(fetch = FetchType.LAZY)
    val batches: MutableList<Batch> = mutableListOf()



)

