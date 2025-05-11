package com.example.imagemanipulator.ios.util

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGSize

/**
 * Extension functions for CoreGraphics types to make them more idiomatic for Kotlin usage
 */
@ExperimentalForeignApi
fun CValue<CGPoint>.getX(): Double = useContents { x }

@ExperimentalForeignApi
fun CValue<CGPoint>.getY(): Double = useContents { y }

@ExperimentalForeignApi
fun CValue<CGSize>.getWidth(): Double = useContents { width }

@ExperimentalForeignApi
fun CValue<CGSize>.getHeight(): Double = useContents { height }

/**
 * Extension functions for CGSize properties that aren't wrapped in CValue
 */
@ExperimentalForeignApi
fun CGSize.getWidth(): Double = width

@ExperimentalForeignApi
fun CGSize.getHeight(): Double = height