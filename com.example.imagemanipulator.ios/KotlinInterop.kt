package com.example.imagemanipulator.ios

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.Unit
import kotlin.concurrent.AtomicReference

/**
 * This file provides the necessary Kotlin interop types for Swift to use.
 * It exports interfaces and utility functions to bridge between Swift and Kotlin.
 */

// Create a dedicated CoroutineScope for flow collection that won't be canceled on exceptions
private val flowCollectionScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

/**
 * A collector interface that Swift can implement to receive values from Kotlin flows.
 */
interface FlowCollector {
    fun emit(value: Any?, completionHandler: (Unit?, Throwable?) -> Unit)
}

/**
 * Kotlin's Unit type exposed for Swift.
 */
class KotlinUnit private constructor() {
    companion object {
        val shared = KotlinUnit()
    }
}

/**
 * Static utility methods for working with Kotlin flows from Swift.
 */
object KotlinFlowKt {
    
    /**
     * Companion object to provide a Swift-accessible singleton instance.
     */
    val companion = KotlinFlowKt
    
    /**
     * Collects values from a Flow and passes them to the collector.
     */
    fun collectFlow(flow: Any, collector: FlowCollector) {
        when (flow) {
            is StateFlow<*> -> {
                // For StateFlow, immediately deliver the current value
                collector.emit(flow.value) { _, error ->
                    if (error != null) {
                        println("Error emitting initial StateFlow value: ${error.message}")
                    }
                }

                // Set up collection for future values
                flowCollectionScope.launch {
                    try {
                        flow.collect { value ->
                            collector.emit(value) { _, _ -> }
                        }
                    } catch (e: Exception) {
                        // Log exception but don't cancel other collections
                        println("Error collecting from StateFlow: ${e.message}")
                    }
                }
            }
            is Flow<*> -> {
                // For regular flows, set up collection
                flowCollectionScope.launch {
                    try {
                        flow.collect { value ->
                            collector.emit(value) { _, _ -> }
                        }
                    } catch (e: Exception) {
                        // Log exception but don't cancel other collections
                        println("Error collecting from Flow: ${e.message}")
                    }
                }
            }
            else -> {
                throw IllegalArgumentException("Flowable is not a Flow or StateFlow")
            }
        }
    }
}