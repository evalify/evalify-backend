package com.evalify.evalifybackend.user.controller

import com.evalify.evalifybackend.user.domain.Role
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.user.domain.dto.CreateUserRequest
import com.evalify.evalifybackend.user.domain.dto.UpdateUserRequest
import com.evalify.evalifybackend.user.exception.InvalidRoleException
import com.evalify.evalifybackend.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/user")
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
        if (role != null) {
            try {
                val roleEnum = Role.valueOf(role.uppercase())
                val paginatedUsers = userService.getAllUsersByRole(roleEnum, page, size, sort_by, sort_order)
                return ResponseEntity.ok(paginatedUsers)
            } catch (e: IllegalArgumentException) {
                throw InvalidRoleException("$role. Valid roles are: ${Role.values().joinToString(", ")}")
            }
        } else {
            val paginatedUsers = userService.getAllUsers(page, size, sort_by, sort_order)
            return ResponseEntity.ok(paginatedUsers)
        }
    }

    @GetMapping("/search")
    fun searchUsers(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String    ): ResponseEntity<Any> {
        val paginatedUsers = userService.searchUsers(query, page, size, sort_by, sort_order)
        return ResponseEntity.ok(paginatedUsers)
    }

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<Any> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }

    @PutMapping("/{userId}")
    fun updateUser(
        @PathVariable userId: String,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<Any> {
        val user = userService.updateUser(userId, request)
        return ResponseEntity.ok(user)
    }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String): ResponseEntity<Any> {
        userService.deleteUser(userId)
        return ResponseEntity.ok(mapOf("message" to "User deleted successfully"))
    }


    @PostMapping("/bulk")
    fun createUsers(@RequestBody users: List<User>): ResponseEntity<List<User>>{
        return ResponseEntity(
            userService.createUsers(users),
            HttpStatus.CREATED
        )
    }    @DeleteMapping("/bulk")
    fun deleteUsers(@RequestBody users: List<String>): ResponseEntity<Any> {
        userService.bulkDeleteUsers(users)
        return ResponseEntity.ok(mapOf("message" to "Users deleted successfully"))
    }
    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: String): ResponseEntity<Any> {
        val user = userService.getUserById(userId)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/students/search")
    fun searchStudents(@RequestParam query: String): ResponseEntity<Any> {
        val students = userService.searchStudents(query)
        return ResponseEntity.ok(students)
    }

    @GetMapping("/faculty")
    fun getAllFaculty(): ResponseEntity<Any> {
        val faculty = userService.getAllUsersByRole(Role.FACULTY, 0, Int.MAX_VALUE, "name", "asc")
        return ResponseEntity.ok(faculty)
    }

    @GetMapping("/faculty/search")
    fun searchFaculty(
        @RequestParam query: String
    ): ResponseEntity<Any> {
        val faculty = userService.searchFaculty(query)
        return ResponseEntity.ok(faculty)
    }

    @GetMapping("/count")
    fun getUserCount(): ResponseEntity<Map<Role, Long>> {
        val userCount = userService.getUserCount()
        return ResponseEntity.ok(userCount)
    }
}