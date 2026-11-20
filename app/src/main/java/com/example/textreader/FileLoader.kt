package com.example.textreader

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Загрузка содержимого файла в фоновом потоке.
 */
class FileLoader(private val context: Context) {

    suspend fun load(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = uri.fileName(context.contentResolver).orEmpty()
            Result.success(BookParser.parse(context, uri, fileName))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
