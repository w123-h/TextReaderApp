package com.example.textreader

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Получение отображаемого имени файла из Uri.
 */
fun Uri.fileName(contentResolver: ContentResolver): String? {
    contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            return cursor.getString(nameIndex)
        }
    }
    return lastPathSegment
}
