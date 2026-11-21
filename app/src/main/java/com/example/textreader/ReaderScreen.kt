package com.example.textreader

import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Экран: разметка, обработка нажатий и отображение состояний (загрузка/текст/ошибка).
 */
class ReaderScreen(private val activity: ComponentActivity) {

    private val openButton: Button
    private val progressBar: ProgressBar
    private val scrollView: ScrollView
    private val textView: TextView
    private val placeholderText: TextView
    private val errorText: TextView

    private val filePicker = FilePicker(activity)
    private val fileLoader = FileLoader(activity.applicationContext)

    init {
        activity.setContentView(R.layout.activity_main)

        openButton = activity.findViewById(R.id.openButton)
        progressBar = activity.findViewById(R.id.progressBar)
        scrollView = activity.findViewById(R.id.scrollView)
        textView = activity.findViewById(R.id.textView)
        placeholderText = activity.findViewById(R.id.placeholderText)
        errorText = activity.findViewById(R.id.errorText)

        val pickFile = { filePicker.open(::onFilePicked) }
        openButton.setOnClickListener { pickFile() }
        placeholderText.setOnClickListener { pickFile() }
    }

    private fun onFilePicked(uri: Uri?) {
        if (uri == null) return

        activity.lifecycleScope.launch {
            showState(ScreenState.Loading)
            val result = fileLoader.load(uri)

            val state = result.fold(
                onSuccess = { text ->
                    if (TextTools.isBlank(text)) {
                        ScreenState.Error(
                            activity.getString(R.string.error_empty_file)
                        )
                    } else {
                        ScreenState.Content(text)
                    }
                },
                onFailure = { error ->
                    ScreenState.Error(
                        activity.getString(R.string.error_read, error.message ?: "")
                    )
                }
            )
            showState(state)
        }
    }

    private fun showState(state: ScreenState) {
        when (state) {
            is ScreenState.Loading -> {
                progressBar.visibility = View.VISIBLE
                openButton.isEnabled = false
                scrollView.visibility = View.GONE
                placeholderText.visibility = View.GONE
                errorText.visibility = View.GONE
            }

            is ScreenState.Content -> {
                progressBar.visibility = View.GONE
                openButton.isEnabled = true
                placeholderText.visibility = View.GONE
                errorText.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                textView.setText(TextTools.normalizeLineBreaks(state.text))
                scrollView.post { scrollView.fullScroll(View.FOCUS_UP) }
            }

            is ScreenState.Error -> {
                progressBar.visibility = View.GONE
                openButton.isEnabled = true
                scrollView.visibility = View.GONE
                placeholderText.visibility = View.GONE
                errorText.text = state.message
                errorText.visibility = View.VISIBLE
            }
        }
    }
}
