package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.CachedFileRepository

class GetAllFilesUseCase(private val repo: CachedFileRepository) {
    suspend fun execute(): List<CachedFile> = repo.getAllFiles()
}