package com.evalify.evalifybackend.topic.domain


import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID


@Entity
@Table(name = "topic")
class Topic(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    val name: String,

    )