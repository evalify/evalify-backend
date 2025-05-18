package com.evalify.evalifybackend.security.utils

import com.evalify.evalifybackend.common.logging.logger
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter


/**
 * This class is responsible for converting a JWT token into a Spring Security `JwtAuthenticationToken`.
 * It extracts roles from the JWT claims and assigns them as authorities to the authentication token.
 *
 * The `convert` method takes a JWT token and returns a `JwtAuthenticationToken` with the extracted authorities.
 */
class KeycloakJwtTokenConverter(
    private val jwtGrantedAuthoritiesConverter: JwtGrantedAuthoritiesConverter,
) : Converter<Jwt, JwtAuthenticationToken> {

    private val logger by logger()

    override fun convert(jwt: Jwt): JwtAuthenticationToken {
        logger.debug("Converting JWT: {}", jwt.claims.keys)

        // Ensure we always have an authenticated user by creating a basic authentication
        val authorities = mutableSetOf<GrantedAuthority>()

        // Add a default USER role to ensure authentication works
        authorities.add(SimpleGrantedAuthority("ROLE_USER"))

        try {
            // Try to extract client roles
            val clientRolesMap = jwt.getClaimAsMap("resource_access")
            clientRolesMap?.forEach { (client, value) ->
                if (value is Map<*, *>) {
                    val rolesList = value["roles"]
                    if (rolesList is Collection<*>) {
                        rolesList.filterIsInstance<String>().forEach { role ->
                            authorities.add(SimpleGrantedAuthority("ROLE_$role"))
                            logger.debug("Added client role: ROLE_$role")
                        }
                    }
                }
            }

            // Try to extract realm roles
            val realmAccess = jwt.getClaimAsMap("realm_access")
            val realmRoles = realmAccess?.get("roles") as? Collection<*>
            realmRoles?.filterIsInstance<String>()?.forEach { role ->
                authorities.add(SimpleGrantedAuthority("ROLE_$role"))
                logger.debug("Added realm role: ROLE_$role")
            }

        } catch (e: Exception) {
            logger.error("Error extracting authorities from JWT", e)
            // Continue with default authorities
        }

        // Get the principal name - prefer the username if available
        val principalName = jwt.getClaimAsString("preferred_username")
            ?: jwt.getClaimAsString(JwtClaimNames.SUB)
            ?: "unknown"

        logger.info("Authenticated user: $principalName with authorities: $authorities")

        return JwtAuthenticationToken(jwt, authorities, principalName)
    }

    companion object {
        private const val KEYCLOAK_ROLE_PREFIX = "ROLE_"
    }
}