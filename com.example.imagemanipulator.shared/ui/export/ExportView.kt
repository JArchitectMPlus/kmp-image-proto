package com.example.imagemanipulator.shared.ui.export

/**
 * Base interface for export view
 * Each platform will provide its own implementation
 */
interface ExportView {
    fun show()
    fun hide()
    fun exportToBase64(): String
}