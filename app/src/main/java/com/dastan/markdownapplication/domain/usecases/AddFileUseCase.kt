package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.repositiry.CachedFileRepository

class AddFileUseCase(private val repo: CachedFileRepository) {
    suspend fun execute(name: String, content: String):Long {
        return repo.saveFile(name, content)
    }
}