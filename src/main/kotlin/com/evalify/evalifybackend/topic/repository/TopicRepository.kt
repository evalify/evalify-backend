package com.evalify.evalifybackend.topic.repository

import com.evalify.evalifybackend.topic.domain.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID


@RepositoryRestResource(path = "topic")
interface TopicRepository : JpaRepository<Topic, UUID> {
}