package com.dastan.markdownapplication.domain.usecases

import android.util.Log
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.CachedFileRepository
import com.dastan.markdownapplication.data.repositiry.MarkdownFileSource

class ImportFileUseCase(
    private val addFile: AddFileUseCase,
    private val repo: CachedFileRepository
) {
    suspend fun execute(source: MarkdownFileSource): CachedFile {
        val (baseName, content) = source.load()
        val id = addFile.execute(baseName, content)
        val finalName = "${baseName.substringBeforeLast(".")}-${id}.md"
        repo.renameFile(id, finalName)
        return CachedFile(name = finalName, content = content, lastOpened = System.currentTimeMillis())
    }
}