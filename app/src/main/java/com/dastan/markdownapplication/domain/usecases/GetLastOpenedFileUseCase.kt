package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.CachedFileRepository

class GetLastOpenedFileUseCase(private val repo: CachedFileRepository) {
    suspend fun execute(): CachedFile? = repo.getLastOpened()
}