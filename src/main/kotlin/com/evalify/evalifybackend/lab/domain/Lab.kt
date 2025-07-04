package com.evalify.evalifybackend.lab.domain
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "lab")
class Lab (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,
    var name: String,
    var block: String,
    var ipSubnet: String,

    @OneToMany(fetch = FetchType.LAZY)
    val labAssistant: List<User> = emptyList(),

    @ManyToMany(mappedBy = "lab")
    val quiz: List<Quiz> = emptyList()
)