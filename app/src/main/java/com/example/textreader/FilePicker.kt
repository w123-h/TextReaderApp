package com.example.textreader

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Обёртка над системным выбором файла (Storage Access Framework).
 */
class FilePicker(activity: ComponentActivity) {

    private var onPicked: ((Uri?) -> Unit)? = null

    private val launcher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onPicked?.invoke(uri)
        }

    fun open(callback: (Uri?) -> Unit) {
        onPicked = callback
        launcher.launch("*/*")
    }
}
