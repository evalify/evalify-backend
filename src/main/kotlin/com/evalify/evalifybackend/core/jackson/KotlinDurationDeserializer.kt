package com.evalify.evalifybackend.core.jackson


import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class KotlinDurationDeserializer : JsonDeserializer<Duration>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Duration {
        val millis = p.longValue // assuming duration is in milliseconds
        return millis.milliseconds
    }
}
