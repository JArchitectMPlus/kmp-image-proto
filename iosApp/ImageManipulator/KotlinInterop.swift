import shared
import Foundation

/**
 * This file provides extensions and utilities for Swift interoperability with Kotlin types.
 * The main types are now directly exported from the shared Kotlin module.
 */

// Extension to make accessing values from StateFlow more convenient in Swift
extension Kotlinx_coroutines_coreStateFlow {
    var currentValue: Any? {
        return self.value
    }
}

// Extension to make flow collection more Swifty
// Helper function to collect flow using the KotlinFlowKt methods
func collectFlow(_ flow: Any, collector: FlowCollector) {
    // Use the method from KotlinFlowKt
    KotlinFlowKt().collectFlow(flow: flow, collector: collector)
}

// Extension to enable closure-based syntax for FlowCollector
extension FlowCollector where Self == FlowCollectorImpl {
    static func callAsFunction(_ callback: @escaping (Any?) -> Void) -> FlowCollector {
        return FlowCollectorImpl(callback: callback)
    }
}