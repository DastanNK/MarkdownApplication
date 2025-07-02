package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.repositiry.EditFileRepository

class UpdateFileUseCase(private val repo: EditFileRepository) {
    suspend fun execute(name: String, text: String) =
        repo.updateContent(name, text)
}