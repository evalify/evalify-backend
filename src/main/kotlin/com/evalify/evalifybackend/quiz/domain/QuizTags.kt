package com.evalify.evalifybackend.quiz.domain

import com.evalify.evalifybackend.semester.domain.Semester
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.Table
import java.util.UUID
import jakarta.persistence.ManyToMany

@Entity
@Table(name = "quiz_tags")
class QuizTags(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val name: String?,
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
)


