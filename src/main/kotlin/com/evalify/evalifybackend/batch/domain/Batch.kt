package com.evalify.evalifybackend.batch.domain

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
    @OneToMany
    val students: List<User>,
    @OneToMany
    val managers: List<User>

)