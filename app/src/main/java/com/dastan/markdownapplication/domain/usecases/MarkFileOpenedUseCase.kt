package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.CachedFileRepository

class MarkFileOpenedUseCase(private val repo: CachedFileRepository) {
    suspend fun execute(file: CachedFile) = repo.markOpened(file)
}