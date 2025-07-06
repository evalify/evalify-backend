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
    @JsonIgnore // Prevent circular reference during JSON serialization
    val labAssistant: List<User> = emptyList(),

    @ManyToMany(mappedBy = "lab")
    @JsonIgnore // Prevent circular reference during JSON serialization
    val quiz: List<Quiz> = emptyList()
)