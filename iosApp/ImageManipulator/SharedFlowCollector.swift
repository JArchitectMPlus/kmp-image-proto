import shared
import Foundation

/// A shared Flow Collector implementation that can be reused by all view model wrappers
class FlowCollectorImpl: NSObject, FlowCollector {
    private let callback: (Any?) -> Void

    init(callback: @escaping (Any?) -> Void) {
        self.callback = callback
        super.init()
    }

    func emit(value: Any?, completionHandler: @escaping (KotlinUnit?, KotlinThrowable?) -> Void) {
        callback(value)
        completionHandler(KotlinUnit(), nil)
    }
}