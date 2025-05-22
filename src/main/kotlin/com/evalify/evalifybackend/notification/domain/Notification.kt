package com.evalify.evalifybackend.notification.domain
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
class Notification (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val title:String,
    val message: String,
    val isViewed: Boolean,

    val createdBy: UUID,
    val createdAt: Instant = Instant.now()
)