package com.evalify.evalifybackend.semester.domain
import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.*
import java.util.*


@Entity
@Table(name = "semester")
 class Semester (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val name: String,
    val year: Int,
    val isActive: Boolean = true,

    @Column(nullable = true)
    @OneToMany(fetch = FetchType.LAZY,cascade = [CascadeType.ALL], mappedBy = "semester")
    val courses: MutableList<Course> = mutableListOf(),


    @Column(nullable = true)
    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(
        name = "semester_managers",
        joinColumns = [JoinColumn(name = "semester_id")],
        inverseJoinColumns = [JoinColumn(name = "manager_id")]
    )
    val managers: MutableList<User> = mutableListOf(),


    // TODO: add Quiz Tags To a Semester
)
