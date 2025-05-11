package com.example.imagemanipulator.shared.transformation

/**
 * Represents a point in 2D space
 */
data class Point(val x: Float, val y: Float) {
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
    operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
    operator fun times(factor: Float): Point = Point(x * factor, y * factor)
    operator fun div(factor: Float): Point = Point(x / factor, y / factor)
}

/**
 * Manages transformations applied to an image
 */
class ImageTransformation {
    var scale: Float = 1.0f
        private set
    
    var rotation: Float = 0.0f
        private set
    
    var offset: Point = Point(0f, 0f)
        private set
    
    /**
     * Apply a scaling transformation
     * @param scaleFactor The factor to scale by (>1 to zoom in, <1 to zoom out)
     * @param minimum The minimum scale allowed
     * @param maximum The maximum scale allowed
     */
    fun applyScale(scaleFactor: Float, minimum: Float = 0.1f, maximum: Float = 5.0f) {
        scale = (scale * scaleFactor).coerceIn(minimum, maximum)
    }
    
    /**
     * Set the scale value directly
     * @param newScale The new scale value
     * @param minimum The minimum scale allowed
     * @param maximum The maximum scale allowed
     */
    fun setScale(newScale: Float, minimum: Float = 0.1f, maximum: Float = 5.0f) {
        scale = newScale.coerceIn(minimum, maximum)
    }
    
    /**
     * Apply a rotation transformation
     * @param degrees The amount to rotate in degrees
     */
    fun applyRotation(degrees: Float) {
        rotation = (rotation + degrees) % 360
        if (rotation < 0) rotation += 360
    }
    
    /**
     * Set the rotation value directly
     * @param degrees The rotation in degrees
     */
    fun setRotation(degrees: Float) {
        rotation = degrees % 360
        if (rotation < 0) rotation += 360
    }
    
    /**
     * Apply a translation
     * @param dx The amount to move horizontally
     * @param dy The amount to move vertically
     */
    fun applyTranslation(dx: Float, dy: Float) {
        offset = Point(offset.x + dx, offset.y + dy)
    }
    
    /**
     * Set the offset directly
     * @param newOffset The new offset
     */
    fun setOffset(newOffset: Point) {
        offset = newOffset
    }
    
    /**
     * Reset all transformations to their default values
     */
    fun reset() {
        scale = 1.0f
        rotation = 0.0f
        offset = Point(0f, 0f)
    }
}