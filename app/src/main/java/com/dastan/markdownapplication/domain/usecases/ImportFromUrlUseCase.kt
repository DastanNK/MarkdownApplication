package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.UrlMarkdownFileSource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class ImportFromUrlUseCase @Inject constructor(
    private val importFile: ImportFileUseCase,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun execute(url: String): CachedFile {
        return importFile.execute(UrlMarkdownFileSource(url, ioDispatcher))
    }
}
