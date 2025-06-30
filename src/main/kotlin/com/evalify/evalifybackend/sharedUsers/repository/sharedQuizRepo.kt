package com.evalify.evalifybackend.sharedUsers.repository

import com.evalify.evalifybackend.sharedUsers.domain.SharedQuiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface sharedQuizRepo : JpaRepository<SharedQuiz, UUID> {

}