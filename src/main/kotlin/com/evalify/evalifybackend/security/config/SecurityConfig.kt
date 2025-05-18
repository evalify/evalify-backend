package com.evalify.evalifybackend.security.config

import com.evalify.evalifybackend.security.utils.JwtAuthenticationEntryPoint
import com.evalify.evalifybackend.security.utils.KeycloakJwtTokenConverter
import com.evalify.evalifybackend.security.utils.RequestDebugFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

// This configuration class is responsible for setting up security filters and CORS configuration
// for the application. It uses Spring Security to manage authentication and authorization.

/*
    This configuration is used for development purposes only.
    It disables CSRF protection and allows all requests.
 */
@Configuration
@Profile("dev")
class DevSecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
        return http.build()
    }
}

/*
    This configuration is used for production purposes.
    It sets up CORS, JWT authentication, and other security features.

    It also configures the security filter chain to handle authentication and authorization.
    The `corsConfigurationSource` method sets up CORS configuration, allowing specific origins and headers.
 */
@Configuration
@Profile("prod")
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(@Autowired private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint) {

    // The allowed origins for CORS requests. This can be set in application properties.
    // If not set, it will deny all origins.
    @Value("\${cors.allowed-origins:}")
    lateinit var allowedOrigins: String

    // The `keycloakJwtTokenConverter` bean is responsible for converting JWT tokens into Spring Security's
    @Bean
    fun keycloakJwtTokenConverter(): KeycloakJwtTokenConverter {
        return KeycloakJwtTokenConverter(JwtGrantedAuthoritiesConverter())
    }

    // The security filter chain is responsible for configuring the security settings for the application.
    // It sets up JWT authentication, CORS configuration, and exception handling.
    // The `evalifyServerFilterChain` method configures the security filter chain for the application.
    @Bean
    @Throws(Exception::class)
    fun evalifyServerFilterChain(http: HttpSecurity): SecurityFilterChain {
        // Configure security with more permissive settings to fix 401 errors
        http
            .csrf { csrf -> csrf.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/api/users/**").permitAll()
                    // .requestMatchers("/api/**").permitAll() // Per                    // .requestMatchers("/api/**").permitAll() // Permit all API endpoints during debuggingmit all API endpoints during debugging
                    .requestMatchers("/error", "/actuator/**").permitAll()
                 .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(keycloakJwtTokenConverter())
                }
                oauth2.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            }
            .exceptionHandling { ex -> 
                ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            }
            .addFilterBefore(RequestDebugFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    // CORS configuration to allow specific origins and headers
    // This is important for cross-origin requests, especially when using JWT authentication.
    // It allows the frontend application to communicate with the backend securely.
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.allowedOrigins = if (allowedOrigins.isBlank()) {
            listOf() // deny all origins
        } else {
            allowedOrigins.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }

        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf(
            "Authorization", "Content-Type", "X-Requested-With",
            "accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        )
        // Expose Authorization header to client
        configuration.exposedHeaders = listOf("Authorization")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
