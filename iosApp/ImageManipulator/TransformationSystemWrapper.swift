import shared
import Foundation
import SwiftUI
import Combine

/**
 * Wrapper for the transformation system to make it observable in SwiftUI
 */
class TransformationSystemWrapper: ObservableObject {
    private let transformationSystem = TransformationSystem()
    
    func addTransformation(transformation: TransformationManager) {
        transformationSystem.addTransformation(transformation: transformation)
    }
}