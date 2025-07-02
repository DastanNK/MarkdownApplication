package com.dastan.markdownapplication.data.repositiry

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UriMarkdownFileSource(
    private val uri: Uri,
    private val contentResolver: ContentResolver
) : MarkdownFileSource {

    override suspend fun load(): Pair<String, String> = withContext(Dispatchers.IO) {
        val name = contentResolver.query(uri, null, null, null, null)?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && index != -1) it.getString(index) else "unnamed.md"
        } ?: "unnamed.md"

        val text = contentResolver.openInputStream(uri)
            ?.use { it.readBytes() }
            ?.toString(Charsets.UTF_8) ?: ""

        name to text
    }
}
