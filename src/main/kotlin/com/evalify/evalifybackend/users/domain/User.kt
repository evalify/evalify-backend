package com.evalify.evalifybackend.users.domain

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Int?,

    @Column(nullable = false)
    private var name: String,

    @Column(nullable = false, unique = true)
    private var email: String,

    @Column(nullable = false)
    private var password: String
    ) {

}