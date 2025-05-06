package com.example.imagemanipulator.shared.model

class LayerHistory {
    private val history: MutableList<Canvas> = mutableListOf()
    private var current: Int = -1
    private val HISTORY_LIMIT = 20

    fun undo(): Canvas? {
        if (current > 0) {
            current--
            return history[current]
        }
        return null
    }

    fun redo(): Canvas? {
        if (current < history.size - 1) {
            current++
            return history[current]
        }
        return null
    }

    fun addState(canvas: Canvas) {
        if (history.size >= HISTORY_LIMIT) {
            history.removeAt(0)
            current--
        }
        history.add(canvas)
        current = history.size - 1
    }
}
