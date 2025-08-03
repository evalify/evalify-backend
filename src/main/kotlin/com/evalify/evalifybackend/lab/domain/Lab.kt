package com.evalify.evalifybackend.lab.domain
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.user.domain.User
import com.fasterxml.jackson.annotation.JsonIgnore
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

    @ManyToMany
    @JoinTable(
        name = "lab_quiz",
        joinColumns = [JoinColumn(name="lab_id")],
        inverseJoinColumns = [JoinColumn(name="quiz_id")]
    )
    val quiz: List<Quiz> = listOf(),
)