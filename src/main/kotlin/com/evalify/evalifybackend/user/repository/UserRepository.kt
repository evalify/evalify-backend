package com.evalify.evalifybackend.usewr.repository
import com.evalify.evalifybackend.user.domain.Role
import com.evalify.evalifybackend.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "user")
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findByProfileId(profileId: String): User?
    fun existsByEmail(email: String): Boolean

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun findByNameOrEmailContainingIgnoreCase(@Param("query") query: String): List<User>

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun searchByNameOrEmailContainingIgnoreCase(@Param("query") query: String, pageable: Pageable): Page<User>    fun findByRole(role: Role): List<User>

    @Query("SELECT u FROM User u WHERE u.role = :role")
    fun findByRolePaged(@Param("role") role: Role, pageable: Pageable): Page<User>

    override fun findAll(pageable: Pageable): Page<User>
}