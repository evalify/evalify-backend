package com.evalify.evalifybackend.batch.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.semester.domain.Semester
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.*
import java.time.Year
import java.util.*

@Entity
@Table(name = "batch")
class Batch (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    val name: String,
    val batch: Year,
    val department: String,
    val section: String,
    val isActive: Boolean,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val semester: List<Semester>,
    @OneToMany(fetch = FetchType.LAZY)
    val students: List<User>,
    @ManyToMany(fetch = FetchType.LAZY)
    val managers: List<User>,

    @ManyToMany(mappedBy = "batch")
    val quiz:List<Quiz>  = listOf(),

    @ManyToMany
    @JoinTable(
        name = "batch_bank",
        joinColumns = [JoinColumn(name="batch_id")],
        inverseJoinColumns = [JoinColumn(name="bank_id")]
    )
    val bank:MutableList<Bank> = mutableListOf()

)