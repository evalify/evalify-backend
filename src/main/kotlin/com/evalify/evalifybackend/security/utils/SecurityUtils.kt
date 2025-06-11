package com.evalify.evalifybackend.security.utils



    import org.springframework.security.core.Authentication
    import org.springframework.security.core.context.SecurityContextHolder
    import org.springframework.security.oauth2.jwt.Jwt

    object SecurityUtils { // Using an object for a static-like utility

        /**
         * Retrieves the authenticated user's ID (sub claim) from the current security context.
         * @return The user ID as a String, or null if no authenticated user or 'sub' claim is found.
         */
        fun getCurrentUserId(): String? {
            val authentication = SecurityContextHolder.getContext().authentication

            return if (authentication != null && authentication.isAuthenticated) {
                // Check if the principal is a Jwt object (typical for OAuth2 Resource Server)
                if (authentication.principal is Jwt) {
                    (authentication.principal as Jwt).subject
                } else {
                    // Fallback for other authentication types if needed
                    authentication.name
                }
            } else {
                null // No authenticated user
            }
        }

        /**
         * Retrieves the entire JWT object from the current security context.
         * @return The Jwt object, or null if no authenticated user or principal is not a Jwt.
         */
        fun getCurrentJwt(): Jwt? {
            val authentication = SecurityContextHolder.getContext().authentication

            return if (authentication != null && authentication.isAuthenticated) {
                authentication.principal as? Jwt
            } else {
                null // No authenticated user
            }
        }

        /**
         * Retrieves a specific claim from the current user's JWT.
         * @param claimName The name of the claim to retrieve (e.g., "email", "preferred_username").
         * @return The claim value as a String, or null if the claim is not found or not a String.
         */
        fun getCurrentJwtClaim(claimName: String): String? {
            val jwt = getCurrentJwt()
            return jwt?.getClaimAsString(claimName)
        }
    }
