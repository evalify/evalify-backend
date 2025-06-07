package com.evalify.evalifybackend.quiz.question.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
class Topic(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    val name:String
) {

}