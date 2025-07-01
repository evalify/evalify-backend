package com.evalify.evalifybackend.quiz.repository


import com.evalify.evalifybackend.quiz.domain.QuizSet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuizSetRepository : JpaRepository<QuizSet, UUID>
