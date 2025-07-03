package com.dastan.markdownapplication.domain.usecases

import android.content.ContentResolver
import android.net.Uri
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.UriMarkdownFileSource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


class ImportFromUriUseCase @Inject constructor(private val importFile: ImportFileUseCase,
                                               private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun execute(uri: Uri, contentResolver: ContentResolver): CachedFile {
        return importFile.execute(UriMarkdownFileSource(uri, contentResolver, ioDispatcher))
    }
}