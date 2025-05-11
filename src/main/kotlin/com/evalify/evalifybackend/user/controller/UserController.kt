package com.evalify.evalifybackend.user.controller

import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController (
    @Autowired
    val userService: UserService,
) {
    @GetMapping("/")
    fun getAllUsers(): ResponseEntity<List<User>> {
        return ResponseEntity(
            userService.getAllUser(),
            HttpStatus.OK
        )
    }

    @PostMapping("/")
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity(
            userService.createNewUser(user),
            HttpStatus.CREATED
        )
    }
}