package com.evalify.evalifybackend.usewr.repository

import com.evalify.evalifybackend.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID


interface UserRepository : JpaRepository<User, UUID> {
}