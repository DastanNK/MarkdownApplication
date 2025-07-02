package com.dastan.markdownapplication.data.model

sealed class Inline {
    data class Text(val value: String) : Inline()
    data class Bold(val value: String) : Inline()
    data class Italic(val value: String) : Inline()
    data class Strike(val value: String) : Inline()
}