package com.evalify.evalifybackend.core.keycloak.controller

import com.evalify.evalifybackend.core.keycloak.service.KeyCloakUserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users/keycloak/sync")
class KeyCloakUserController(
    private val keyCloakUserService: KeyCloakUserService
) {
    @PostMapping
    fun syncUsers(): ResponseEntity<String> {
      return ResponseEntity.ok( keyCloakUserService.syncUsers())
    }
}