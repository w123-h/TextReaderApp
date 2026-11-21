package com.example.textreader

import android.content.Context
import android.net.Uri
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import kotlin.text.charset

/**
 * Читает содержимое выбранного файла.
 */
object BookParser {

    fun parse(context: Context, uri: Uri, fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()

        return when (extension) {
            "fb2" -> parseFb2(context, uri)
            "txt" -> readAsText(context, uri)
            else -> {
                // Если расширение неизвестно, пробуем прочитать как текст.
                // Если внутри окажется FB2 — разберём его как XML.
                val text = readAsText(context, uri)
                if (text.contains("<FictionBook", ignoreCase = true)) {
                    parseFb2(context, uri)
                } else {
                    text
                }
            }
        }
    }

    private fun readAsText(context: Context, uri: Uri): String {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Не удалось открыть файл")

        input.use { stream ->
            val bytes = stream.readBytes()

            // Большинство txt в UTF-8, но старые файлы часто в Windows-1251.
            val utf8 = String(bytes, Charsets.UTF_8)
            val text = if (utf8.contains('\uFFFD')) {
                String(bytes, charset("windows-1251"))
            } else {
                utf8
            }

            // Стандартные текстовые API: убираем BOM и приводим переводы строк.
            if (TextTools.isEmpty(text)) return ""
            return TextTools.normalizeLineBreaks(TextTools.stripBom(text))
        }
    }

    /**
     * FB2 — это XML. Извлекаем текст из раздела <Body>: параграфы <p>,
     * заголовки <title> и границы <section> преобразуем в переводы строк.
     */
    private fun parseFb2(context: Context, uri: Uri): String {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Не удалось открыть FB2-файл")

        val parser = Xml.newPullParser()
        val result = StringBuilder()

        input.use { stream ->
            // null = кодировку берём из XML-декларации самого файла
            parser.setInput(stream, null)

            var inBody = false
            var event = parser.eventType

            while (event != XmlPullParser.END_DOCUMENT) {
                when (event) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name.equals("body", ignoreCase = true)) {
                            inBody = true
                        }
                    }

                    XmlPullParser.TEXT -> {
                        if (inBody) {
                            result.append(parser.text)
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tag = parser.name
                        if (inBody && (
                                tag.equals("p", ignoreCase = true) ||
                                tag.equals("title", ignoreCase = true) ||
                                tag.equals("section", ignoreCase = true)
                            )
                        ) {
                            result.appendLine()
                        }
                        if (tag.equals("body", ignoreCase = true)) {
                            inBody = false
                        }
                    }
                }
                event = parser.next()
            }
        }

        return TextTools.normalizeLineBreaks(result.toString())
    }
}
