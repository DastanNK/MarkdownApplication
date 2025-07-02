package com.dastan.markdownapplication.data.model

sealed class MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class Paragraph(val segments: List<Inline>) : MarkdownBlock()
    data class Table(
        val header: List<String>,
        val rows: List<List<String>>
    ) : MarkdownBlock()
    data class Image(val alt: String, val url: String) : MarkdownBlock()
}