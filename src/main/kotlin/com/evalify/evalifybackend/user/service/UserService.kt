package com.evalify.evalifybackend.user.service

import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.core.pagination.PaginationInfo
import com.evalify.evalifybackend.user.domain.Role
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.user.domain.dto.CreateUserRequest
import com.evalify.evalifybackend.user.domain.dto.UpdateUserRequest
import com.evalify.evalifybackend.user.domain.dto.UserResponse
import com.evalify.evalifybackend.user.exception.*
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import kotlin.toString

@Service
class UserService(
    @Autowired
    val userRepository: UserRepository,
) {
    fun getAllUsers(page: Int, size: Int, sortBy: String = "name", sortOrder: String = "asc"): PaginatedResponse<UserResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable = PageRequest.of(page, size, sort)
        val userPage: Page<User> = userRepository.findAll(pageable)

        return PaginatedResponse(
            data = userPage.content.map { it.toUserResponse() },
            pagination = PaginationInfo(
                current_page = page + 1,
                per_page = size,
                total_pages = userPage.totalPages,
                total_count = userPage.totalElements.toInt()
            )
        )
    }

    fun searchUsers(query: String, page: Int, size: Int, sortBy: String = "name", sortOrder: String = "asc"): PaginatedResponse<UserResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable = PageRequest.of(page, size, sort)
        val userPage: Page<User> = userRepository.searchByNameOrEmailContainingIgnoreCase(query, pageable)

        return PaginatedResponse(
            data = userPage.content.map { it.toUserResponse() },
            pagination = PaginationInfo(
                current_page = page + 1,
                per_page = size,
                total_pages = userPage.totalPages,
                total_count = userPage.totalElements.toInt()
            )
        )
    }

    fun getAllUsersByRole(role: Role, page: Int, size: Int, sortBy: String = "name", sortOrder: String = "asc"): PaginatedResponse<UserResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable = PageRequest.of(page, size, sort)
        val userPage: Page<User> = userRepository.findByRolePaged(role, pageable)

        return PaginatedResponse(
            data = userPage.content.map { it.toUserResponse() },
            pagination = PaginationInfo(
                current_page = page + 1,
                per_page = size,
                total_pages = userPage.totalPages,
                total_count = userPage.totalElements.toInt()
            )
        )
    }

    private fun createSort(sortBy: String, sortOrder: String): Sort {
        val direction = if (sortOrder.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        return Sort.by(direction, sortBy)
    }

    fun getAllUsers(): List<UserResponse>{
        return userRepository.findAll().map { user -> user.toUserResponse() }
    }
    fun searchUsers(query: String): List<UserResponse> {
        return userRepository.findByNameOrEmailContainingIgnoreCase(query).map { user -> user.toUserResponse() }
    }

    fun searchStudents(query: String): List<UserResponse> {
        return userRepository.findByNameOrEmailContainingIgnoreCase(query)
            .filter { it.role == Role.STUDENT }
            .map { it.toUserResponse() }
    }

    fun getAllUsersByRole(role: Role): List<UserResponse> {
        return userRepository.findByRole(role).map { user -> user.toUserResponse() }
    }

    fun createUser(request: CreateUserRequest): UserResponse {
        // Validate input
        if (request.name.isBlank()) {
            throw UserValidationException("User name cannot be blank")
        }
        if (request.email.isBlank()) {
            throw UserValidationException("User email cannot be blank")
        }
        if (request.password.isBlank()) {
            throw UserValidationException("User password cannot be blank")
        }

        // Check if user already exists
        if (userRepository.existsByEmail(request.email)) {
            throw UserAlreadyExistsException(request.email)
        }

        try {
            val user = User(
                name = request.name,
                email = request.email,
                password = request.password,
                role = request.role,
                phoneNumber = request.phoneNumber,
                isActive = request.isActive,
                createdAt = Timestamp.from(Instant.now())
            )

            val savedUser = userRepository.save(user)
            return savedUser.toUserResponse()
        } catch (e: Exception) {
            when (e) {
                is UserValidationException, is UserAlreadyExistsException -> throw e
                else -> throw UserServiceException("Failed to create user", e)
            }
        }
    }

    fun updateUser(userId: String, request: UpdateUserRequest): UserResponse {
        // Validate input
        if (request.name.isBlank()) {
            throw UserValidationException("User name cannot be blank")
        }
        if (request.email.isBlank()) {
            throw UserValidationException("User email cannot be blank")
        }

        val user = userRepository.findById(userId).orElseThrow {
            UserNotFoundException(userId)
        }

        // Check if email is being changed and if it's already taken by another user
        if (request.email != user.email && userRepository.existsByEmail(request.email)) {
            throw UserAlreadyExistsException(request.email)
        }

        try {
            user.name = request.name
            user.email = request.email
            user.phoneNumber = request.phoneNumber
            user.role = request.role
            user.isActive = request.isActive

            val updatedUser = userRepository.save(user)
            return updatedUser.toUserResponse()
        } catch (e: Exception) {
            when (e) {
                is UserNotFoundException, is UserValidationException, is UserAlreadyExistsException -> throw e
                else -> throw UserServiceException("Failed to update user", e)
            }
        }
    }    fun deleteUser(userId: String) {
        try {
            val user = userRepository.findById(userId).orElseThrow {
                UserNotFoundException(userId)
            }
            userRepository.delete(user)
        } catch (e: Exception) {
            when (e) {
                is UserNotFoundException -> throw e
                else -> throw UserServiceException("Failed to delete user", e)
            }
        }
    }

    fun bulkDeleteUsers(userIds: List<String>) {
        if (userIds.isEmpty()) {
            throw UserValidationException("User IDs list cannot be empty")
        }
        
        try {
            val users = userRepository.findAllById(userIds)
            if (users.isEmpty()) {
                throw UsersNotFoundException(userIds)
            }
            if (users.size != userIds.size) {
                val foundIds = users.map { it.id }
                val missingIds = userIds.filter { !foundIds.contains(it) }
                throw UsersNotFoundException(missingIds)
            }
            userRepository.deleteAll(users)
        } catch (e: Exception) {
            when (e) {
                is UserValidationException, is UsersNotFoundException -> throw e
                else -> throw BulkOperationException("Failed to delete users", e)
            }
        }
    }

    fun createUser(user: User): User{
        return userRepository.save(user)
    }

    fun getUserById(userId: String): UserResponse {
        val user = userRepository.findById(userId).orElseThrow {
            UserNotFoundException(userId)
        }
        return user.toUserResponse()
    }

    fun createUsers(users: List<User>): List<User> {
        if (users.isEmpty()) {
            throw UserValidationException("Users list cannot be empty")
        }
        
        // Validate each user
        users.forEach { user ->
            if (user.name.isBlank()) {
                throw UserValidationException("User name cannot be blank")
            }
            if (user.email.isBlank()) {
                throw UserValidationException("User email cannot be blank")
            }
            if (userRepository.existsByEmail(user.email)) {
                throw UserAlreadyExistsException(user.email)
            }
        }
        
        try {
            return userRepository.saveAll(users)
        } catch (e: Exception) {
            when (e) {
                is UserValidationException, is UserAlreadyExistsException -> throw e
                else -> throw BulkOperationException("Failed to create users", e)
            }
        }
    }

    @Transactional(readOnly = true)
    fun searchFaculty(query: String): List<UserResponse> {
        // Get all users with role FACULTY whose name or email contains the query
        val allUsers = userRepository.findByNameOrEmailContainingIgnoreCase(query)
        val facultyUsers = allUsers.filter { it.role == Role.FACULTY }

        return facultyUsers.map { it.toUserResponse() }
    }

    fun getUserCount(): Map<Role, Long> {
        return userRepository.countAllUsers().associate {
            val role = it[0] as Role
            val count = (it[1] as Number).toLong()
            role to count
        }
    }

}

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id?.toString(),
        name = this.name,
        email = this.email,
        profileId = this.profileId,
        image = this.image,
        password = this.password,
        role = this.role,
        phoneNumber = this.phoneNumber,
        isActive = this.isActive,
        createdAt = this.createdAt.toString()
    )
}