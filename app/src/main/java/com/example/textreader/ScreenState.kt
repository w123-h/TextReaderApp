package com.example.textreader

/**
 * Возможные состояния экрана.
 */
sealed class ScreenState {
    data object Loading : ScreenState()
    data class Content(val text: String) : ScreenState()
    data class Error(val message: String) : ScreenState()
}
