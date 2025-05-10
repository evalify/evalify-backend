package com.evalify.evalifybackend.users.repositories

import com.evalify.evalifybackend.users.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize

@RepositoryRestResource(path="users")
interface UserRepository : JpaRepository<User, Int> {
    // Allow access to findAll by anyone for debugging
    @Override
    override fun findAll(): List<User>
    
    // All other methods will inherit the security configuration
}
