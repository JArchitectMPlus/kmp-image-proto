import shared
import Foundation

// Re-expose the Objective-C interfaces as Swift types
public typealias Canvas = shared.Canvas
public typealias Layer = shared.Layer
public typealias ImageLayer = shared.ImageLayer
public typealias TextLayer = shared.TextLayer
public typealias TransformationManager = shared.TransformationManager
public typealias PositionTransformation = shared.PositionTransformation
public typealias ScaleTransformation = shared.ScaleTransformation
public typealias RotationTransformation = shared.RotationTransformation
public typealias TransformationSystem = shared.TransformationSystem
public typealias CanvasViewModel = shared.CanvasViewModel
public typealias LayerViewModel = shared.LayerViewModel
public typealias ImageViewModel = shared.ImageViewModel

// These types are explicitly referenced with the 'shared' prefix to avoid any ambiguity
public typealias KotlinFlowKt = shared.KotlinFlowKt
public typealias FlowCollector = shared.FlowCollector
public typealias KotlinUnit = shared.KotlinUnit