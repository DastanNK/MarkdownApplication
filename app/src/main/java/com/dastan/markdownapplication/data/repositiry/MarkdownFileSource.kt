package com.dastan.markdownapplication.data.repositiry



interface MarkdownFileSource {
    suspend fun load(): Pair<String, String>
}