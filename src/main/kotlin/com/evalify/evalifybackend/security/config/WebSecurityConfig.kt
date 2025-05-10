package com.evalify.evalifybackend.security.config

import com.evalify.evalifybackend.security.utils.KeycloakJwtTokenConverter
import com.evalify.evalifybackend.security.utils.RequestDebugFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
import java.util.*

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    private var keycloakJwtTokenConverter: KeycloakJwtTokenConverter? =
        KeycloakJwtTokenConverter(JwtGrantedAuthoritiesConverter())

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
                    // .requestMatchers("/api/**").permitAll() // Permit all API endpoints during debugging
                    .requestMatchers("/error", "/actuator/**").permitAll()
                    // .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(keycloakJwtTokenConverter)
                }
            }
            .addFilterBefore(RequestDebugFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        // Allow both HTTP and HTTPS
        configuration.allowedOrigins = listOf("http://172.17.9.74", "https://172.17.9.74")
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
