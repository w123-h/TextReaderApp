package com.example.textreader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Точка входа: создать экран и передать ему управление.
 * Вся логика вынесена в отдельные классы.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReaderScreen(this)
    }
}
