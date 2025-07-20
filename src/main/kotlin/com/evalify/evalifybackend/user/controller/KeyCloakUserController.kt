package com.evalify.evalifybackend.user.controller

import com.evalify.evalifybackend.user.service.KeyCloakUserService
import com.evalify.evalifybackend.user.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users/keycloak/sync")
class KeyCloakUserController(
    private val keyCloakUserService: KeyCloakUserService
) {
    @GetMapping
    fun syncUsers(){
        keyCloakUserService.syncUsers()
    }
}