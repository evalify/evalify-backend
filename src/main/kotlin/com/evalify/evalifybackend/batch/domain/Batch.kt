package com.evalify.evalifybackend.batch.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.department.domain.Department
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.semester.domain.Semester
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.*
import java.time.Year
import java.util.*

@Entity
@Table(name = "batch")
class Batch(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID ?= null,
    var name: String,
    var graduationYear: Year,
    var section: String,
    var isActive: Boolean,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var semester: MutableSet<Semester> = mutableSetOf(),

    @OneToMany(fetch = FetchType.LAZY)
    var students: MutableSet<User> = mutableSetOf(),

    @ManyToMany(fetch = FetchType.LAZY)
    var managers: MutableSet<User> = mutableSetOf(),

    @ManyToMany(mappedBy = "batch")
    var quiz: MutableSet<Quiz> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "batch_bank",
        joinColumns = [JoinColumn(name = "batch_id")],
        inverseJoinColumns = [JoinColumn(name = "bank_id")]
    )
    var bank: MutableList<Bank> = mutableListOf(),


    @ManyToOne
    @JoinColumn(name = "department_id")
    var department: Department? = null
)