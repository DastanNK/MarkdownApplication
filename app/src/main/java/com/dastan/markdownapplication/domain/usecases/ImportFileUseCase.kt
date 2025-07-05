package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.CachedFileRepository
import com.dastan.markdownapplication.data.repositiry.MarkdownFileSource

class ImportFileUseCase(
    private val renameFile: RenameFileUseCase,
    private val repo: CachedFileRepository
) {
    suspend fun execute(source: MarkdownFileSource): CachedFile {
        val (baseName, content) = source.load()
        val (id, finalName)=renameFile.execute(baseName)
        val currentTime=System.currentTimeMillis()
        repo.saveFile(id, finalName, content)
        return CachedFile(name = finalName, content = content, lastOpened = currentTime)
    }
}