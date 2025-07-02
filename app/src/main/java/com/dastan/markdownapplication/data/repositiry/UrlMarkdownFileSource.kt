package com.dastan.markdownapplication.data.repositiry

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class UrlMarkdownFileSource(
    private val url: String
) : MarkdownFileSource {

    override suspend fun load(): Pair<String, String> = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as? HttpURLConnection
            ?: throw IllegalArgumentException("Invalid URL")

        val text = connection.inputStream.bufferedReader().use { it.readText() }
        val name = url.substringAfterLast("/").takeIf { it.isNotBlank() } ?: "remote.md"

        name to text
    }
}