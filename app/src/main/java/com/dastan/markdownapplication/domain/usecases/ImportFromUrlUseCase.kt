package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.UrlMarkdownFileSource

class ImportFromUrlUseCase(
    private val importFile: ImportFileUseCase
) {
    suspend fun execute(url: String): CachedFile {
        return importFile.execute(UrlMarkdownFileSource(url))
    }
}
