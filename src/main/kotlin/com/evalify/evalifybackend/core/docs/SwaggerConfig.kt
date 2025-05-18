package com.evalify.evalifybackend.core.docs

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info as OasInfo
import io.swagger.v3.oas.models.info.Contact as OasContact
import io.swagger.v3.oas.models.info.License as OasLicense
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


//import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
//import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
//import io.swagger.v3.oas.annotations.security.SecurityRequirement
//import io.swagger.v3.oas.annotations.security.SecurityScheme

/**
 * Swagger configuration for the Evalify API.
 *
 * This configuration sets up the OpenAPI documentation for the API, including
 * the title, version, and description.
 *
 * @see <a href="https://swagger.io/docs/specification/about/">Swagger Documentation</a>
 */

@Configuration
@Profile("dev")
@OpenAPIDefinition(
    info = Info(
        title = "Evalify API",
        version = "1.0.0",
        description = "API documentation for the Evalify Backend",
        contact = Contact(name = "Evalify Team", email = "support@evalify.com", url = "http://evalify.amrita.edu"),
        license = License(name = "Apache 2.0", url = "https://springdoc.org")
    ),
    servers = [Server(url = "http://localhost:8080", description = "Local Dev Server")],
//    security = [SecurityRequirement(name = "BearerAuth")],
)
/*
    Uncomment the lines to enable security scheme for JWT authentication
    Security is not enabled because '/docs' can be accessed only in dev profile
*/
//@SecurityScheme(
//    name = "BearerAuth",
//    type = SecuritySchemeType.HTTP,
//    scheme = "bearer",
//    bearerFormat = "JWT",
//    `in` = SecuritySchemeIn.HEADER
//)
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                OasInfo()
                    .title("Evalify API")
                    .version("1.0.0")
                    .description("API documentation for the Evalify Backend")
                    .contact(OasContact().name("Evalify Team").email("support@evalify.com").url("http://evalify.amrita.edu"))
                    .license(OasLicense().name("Apache 2.0").url("htts://springdoc.org"))
            )
    }
}
