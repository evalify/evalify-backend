package com.evalify.evalifybackend.core.config

import io.github.cdimascio.dotenv.dotenv

/**
 * Provides functionality to load environment variables from .env files
 * based on the current Spring profile.
 */
object DotenvLoader {
    /**
     * Loads environment variables into system properties from the relevant .env file.
     * The file is determined by the current Spring profile.
     *
     * @param profile The Spring profile to load the .env file for. Defaults to "dev".
     */
    private val profile = System.getProperty("spring.profiles.active") ?: "dev";

    fun load() {
        val dotenv = dotenv {
            filename = ".env.$profile"
            ignoreIfMalformed = true
            ignoreIfMissing = true
        }

        dotenv.entries().forEach { entry ->
            System.setProperty(entry.key, entry.value)
        }

        println("Loaded dotenv: .env.$profile")
    }
}
