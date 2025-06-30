package com.evalify.evalifybackend.sharedUsers.domain

import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "shared_quiz")
class SharedQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id : UUID? = null

    @OneToOne
    val owner : User? = null

    @OneToMany
    val guests : MutableList<User> = mutableListOf()





}