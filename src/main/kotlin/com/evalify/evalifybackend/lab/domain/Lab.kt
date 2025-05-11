package com.evalify.evalifybackend.lab.domain

import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "lab")
class Lab (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    val name: String,
    val block: String,
    val ipSubnet: String,

    @OneToMany(fetch = FetchType.LAZY)
    val labAssistant: List<User>
)