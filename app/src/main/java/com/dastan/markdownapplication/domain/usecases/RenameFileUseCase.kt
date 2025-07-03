package com.dastan.markdownapplication.domain.usecases

import java.util.*

class RenameFileUseCase {
    fun execute(baseName: String): Pair<String, String> {
        val id = UUID.randomUUID().toString().take(8)
        val name = "${baseName.substringBeforeLast(".")}-$id.md"
        return id to name
    }
}
