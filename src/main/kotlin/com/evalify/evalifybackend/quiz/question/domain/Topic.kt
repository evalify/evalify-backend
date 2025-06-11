package com.evalify.evalifybackend.quiz.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import jakarta.persistence.*
import java.util.UUID

@Entity
class Topic(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    val name:String,

    @ManyToOne
    val bank : Bank
) {

}