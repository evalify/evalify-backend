package com.evalify.evalifybackend.user.service

import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired
    val userRepository: UserRepository,
) {
    fun getAllUser(): List<User> {
        return userRepository.findAll()
    }

    fun createNewUser(user: User): User {
        return userRepository.save(user)
    }
}