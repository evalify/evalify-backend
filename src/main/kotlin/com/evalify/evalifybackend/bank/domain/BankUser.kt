package com.evalify.evalifybackend.bank.domain

import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedTags
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable
import java.util.UUID


@Embeddable
data class BankUserId(
    var bankId: UUID? = null,
    var userId: String? = null
) : Serializable

@Entity
@Table(name = "bank_users")
class BankUser(

    @EmbeddedId
    var id: BankUserId = BankUserId(),

    @ManyToOne
    @MapsId("bankId")
    @JoinColumn(name = "bank_id")
    var bank: Bank? = null,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Column(name = "tags")
    val tags : SharedTags

)