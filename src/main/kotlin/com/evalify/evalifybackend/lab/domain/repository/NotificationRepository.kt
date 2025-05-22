package com.evalify.evalifybackend.lab.domain.repository

import com.evalify.evalifybackend.notification.domain.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID


@RepositoryRestResource(path = "notification")
interface NotificationRepository : JpaRepository<Notification, UUID>{
}