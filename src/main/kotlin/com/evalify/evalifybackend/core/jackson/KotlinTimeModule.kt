package com.evalify.evalifybackend.core.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import kotlin.time.Duration

class KotlinTimeModule : SimpleModule("KotlinTimeModule") {
    init {
        addDeserializer(Duration::class.java, KotlinDurationDeserializer())
    }
}