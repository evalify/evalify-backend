package com.evalify.evalifybackend.user.controller

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.user.domain.Role
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.user.domain.dto.CreateUserRequest
import com.evalify.evalifybackend.user.domain.dto.UpdateUserRequest
import com.evalify.evalifybackend.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/user")
class UserController (private val userService: UserService)
{
    @GetMapping
    fun getAllUsers(
        @RequestParam(required = false) role: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        return try {
            if (role != null) {
                try {
                    val roleEnum = Role.valueOf(role.uppercase())
                    val paginatedUsers = userService.getAllUsersByRole(roleEnum, page, size, sort_by, sort_order)
                    ResponseEntity.ok(paginatedUsers)
                } catch (e: IllegalArgumentException) {
                    ResponseEntity.badRequest().body(mapOf("message" to "Invalid role: $role. Valid roles are: ${Role.values().joinToString(", ")}"))
                }
            } else {
                val paginatedUsers = userService.getAllUsers(page, size, sort_by, sort_order)
                ResponseEntity.ok(paginatedUsers)
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to fetch users: ${e.message}"))
        }
    }

    @GetMapping("/search")
    fun searchUsers(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String    ): ResponseEntity<Any> {
        return try {
            val paginatedUsers = userService.searchUsers(query, page, size, sort_by, sort_order)
            ResponseEntity.ok(paginatedUsers)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to search users: ${e.message}"))
        }
    }

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<Any> {
        return try {
            val user = userService.createUser(request)
            ResponseEntity.status(HttpStatus.CREATED).body(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to create user: ${e.message}"))
        }
    }

    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: String,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<Any> {
        return try {
            val user = userService.updateUser(userId, request)
            ResponseEntity.ok(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to e.message))
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to update user: ${e.message}"))
        }
    }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String): ResponseEntity<Any> {
        return try {
            userService.deleteUser(userId)
            ResponseEntity.ok(mapOf("message" to "User deleted successfully"))
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to delete user: ${e.message}"))        }    }


    @PostMapping("/bulk")
    fun createUsers(@RequestBody users: List<User>): ResponseEntity<List<User>>{
        return ResponseEntity(
            userService.createUsers(users),
            HttpStatus.CREATED
        )
    }    @DeleteMapping("/bulk")
fun deleteUsers(@RequestBody users: List<String>): ResponseEntity<Any> {
    return ResponseEntity(
        userService.bulkDeleteUsers(users),
        HttpStatus.OK
    )
}
    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: String): ResponseEntity<Any> {
        return try {
            val user = userService.getUserById(userId)
            ResponseEntity.ok(user)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to fetch user: ${e.message}"))
        }
    }

    @GetMapping("/students/search")
    fun searchStudents(@RequestParam query: String): ResponseEntity<Any> {
        return try {
            val students = userService.searchStudents(query)
            ResponseEntity.ok(students)
        } catch (e: Exception) {            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("message" to "Failed to search students: ${e.message}"))
        }
    }

    @GetMapping("/faculty")
    fun getAllFaculty(): ResponseEntity<Any> {
        return try {
            val faculty = userService.getAllUsersByRole(Role.FACULTY, 0, Int.MAX_VALUE, "name", "asc")
            ResponseEntity.ok(faculty)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to fetch faculty: ${e.message}"))
        }
    }

    @GetMapping("/faculty/search")
    fun searchFaculty(
        @RequestParam query: String
    ): ResponseEntity<Any> {
        return try {
            val faculty = userService.searchFaculty(query)
            ResponseEntity.ok(faculty)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to search faculty: ${e.message}"))
        }
    }
}