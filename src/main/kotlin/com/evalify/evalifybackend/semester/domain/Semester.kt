package com.evalify.evalifybackend.semester.domain

import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.*

import java.util.*


@Entity
@Table(name = "semester")
class Semester (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    val name: String,
    val year: Int,
    val isActive: Boolean = true,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val courses: List<Course>,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val managers: List<User>,


    // TODO: add Quiz Tags To a Semester
)
