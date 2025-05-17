package com.evalify.evalifybackend.common.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A property delegate that provides a logger instance using the runtime class of the host object.
 *
 * This is especially useful when used inside interfaces, abstract classes, or anonymous objects,
 * where the concrete class name isn't statically known.
 *
 * ### Usage:
 * ```kotlin
 * class MyService {
 *     private val logger by logger()
 *
 *     fun process() {
 *         logger.info("Processing started")
 *     }
 * }
 * ```
 *
 * This will internally use:
 * `LoggerFactory.getLogger(MyService::class.java)`
 *
 * The logger is named according to the actual class that the object belongs to at runtime.
 */
class RuntimeLoggerDelegate : ReadOnlyProperty<Any, Logger> {

    /**
     * Called when the logger property is accessed. Returns a Logger instance
     * associated with the runtime class of [thisRef].
     *
     * @param thisRef The object owning the delegated property.
     * @param property Metadata about the delegated property (unused).
     * @return A SLF4J Logger for the runtime class of the object.
     */
    override fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        return LoggerFactory.getLogger(thisRef::class.java)
    }
}

/**
 * Returns a logger delegate that uses the runtime class of the object.
 *
 * This should be used with the Kotlin `by` keyword to provide a clean, reusable logger definition.
 *
 * ### Example:
 * ```kotlin
 * private val logger by logger()
 * ```
 */
fun logger(): ReadOnlyProperty<Any, Logger> = RuntimeLoggerDelegate()

/**
 * Returns a static logger for the reified class [T].
 *
 * This is slightly more performant than [logger()] since it avoids reflection,
 * but it cannot dynamically adapt to interfaces, abstract classes, or anonymous types.
 *
 * ### Example:
 * ```kotlin
 * private val logger = staticLogger<MyService>()
 * ```
 *
 * @return A SLF4J Logger bound to the compile-time class [T].
 */
inline fun <reified T : Any> staticLogger(): Logger = LoggerFactory.getLogger(T::class.java)
