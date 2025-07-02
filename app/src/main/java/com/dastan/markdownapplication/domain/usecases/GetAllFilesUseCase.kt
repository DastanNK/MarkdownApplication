package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.CachedFileRepository
import kotlinx.coroutines.flow.Flow

class GetAllFilesUseCase(private val repo: CachedFileRepository) {
    fun execute(): Flow<List<CachedFile>> = repo.getAllFiles()
}