package com.evalify.evalifybackend.quiz.domain

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.Column
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedTags
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.UUID

@Embeddable
data class QuizUserId(
    var quizId: UUID? = null,
    var userId: String? = null
) : Serializable

@Entity
@Table(name = "quiz_users")
class QuizUser(

    @EmbeddedId
    var id: QuizUserId = QuizUserId(),

    @ManyToOne
    @MapsId("quizId")
    @JoinColumn(name = "quiz_id")
    var quiz: Quiz? = null,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Column(name = "tags")
    val tags : SharedTags

)