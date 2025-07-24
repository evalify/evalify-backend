package com.evalify.evalifybackend.core.keycloak.service

import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.user.domain.Role
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.user.repository.UserRepository
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class KeyCloakUserService(
    @Value("\${keycloak.server-url}")
    private var serverUrl: String,
    @Value("\${keycloak.realm}") private val realm: String,
    @Value("\${keycloak.client-id}") private val clientId: String,
    @Value("\${keycloak.username}") private val username: String,
    @Value("\${keycloak.password}") private val password: String,
    private val userRepository: UserRepository,
) {
    private val logger by logger()

    fun syncUsers():String{
        val keyCloak: Keycloak = KeycloakBuilder.builder()
                                            .serverUrl(serverUrl)
                                            .clientId(clientId)
                                            .password(password)
                                            .realm("master")
                                            .username(username)
                                            .build()

        val users = keyCloak.realm(realm).users().list()
        var count:Int = 0

        users.forEach { user ->
            val existingUser = userRepository.findById(user.id).orElse(null)
            if(existingUser == null) {
                val userResource = keyCloak.realm(realm).users().get(user.id)
                val groups = userResource.groups().map { it.name }
                val newUser = User(
                    id = user.id,
                    name = user.username,
                    email = user.email,
                    role = Role.valueOf(groups[0].uppercase()),
                    phoneNumber = ""
                )
                userRepository.save(newUser)
                count += 1
                logger.info("User ${user.id} created.")
            }
        }
        //closes the keycloak connection
        keyCloak.close()
        return "$count users synced"
    }
}