package com.evalify.evalifybackend

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EvalifyBackendApplication

fun main(args: Array<String>) {
    runApplication<EvalifyBackendApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
