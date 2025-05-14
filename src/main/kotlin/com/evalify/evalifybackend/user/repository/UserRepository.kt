package com.evalify.evalifybackend.usewr.repository
import com.evalify.evalifybackend.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "user")
interface UserRepository : JpaRepository<User, UUID> {
}