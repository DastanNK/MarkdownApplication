package com.dastan.markdownapplication.data.model

sealed class Inline {
    data class Text(val text: String) : Inline()
    data class Bold(val content: List<Inline>) : Inline()
    data class Italic(val content: List<Inline>) : Inline()
    data class Strike(val content: List<Inline>) : Inline()
}