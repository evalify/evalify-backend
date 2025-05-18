package com.evalify.evalifybackend.core.config

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
 * Logs the active profiles when the application starts.
 */
@Component
class ProfileLogger(private val env: Environment) : ApplicationRunner {
    /**
     * Logs active Spring profiles to the console.
     */
    override fun run(args: ApplicationArguments?) {
        val activeProfiles = env.activeProfiles
        println("🚀 Server is starting with active profile(s): ${activeProfiles.joinToString(", ")}")
    }
}
