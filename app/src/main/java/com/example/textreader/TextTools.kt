package com.example.textreader

import android.text.TextUtils

/**
 * Работа с текстом на стандартных API Android/Kotlin:
 * android.text.TextUtils и kotlin.text.
 */
object TextTools {

    /** Пустой или состоящий только из пробелов текст (стандартный API Android). */
    fun isBlank(value: String?): Boolean = TextUtils.getTrimmedLength(value) == 0

    /** Проверка на null или нулевую длину (стандартный API Android). */
    fun isEmpty(value: String?): Boolean = TextUtils.isEmpty(value)

    /** Приводит переводы строк CRLF/CR к LF (kotlin.text.Regex). */
    fun normalizeLineBreaks(value: String): String =
        value.replace(Regex("\\r\\n|\\r"), "\n")

    /** Убирает BOM в начале файла, если он есть. */
    fun stripBom(value: String): String = value.trimStart('\uFEFF')
}
