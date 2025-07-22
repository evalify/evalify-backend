package com.evalify.evalifybackend.core.keycloak.controller

import com.evalify.evalifybackend.core.keycloak.service.KeyCloakUserService
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