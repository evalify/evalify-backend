package com.evalify.evalifybackend.topic.repository

import com.evalify.evalifybackend.quiz.question.domain.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface TopicRepo : JpaRepository<Topic, UUID> {  }