package com.dastan.markdownapplication.domain.usecases

import android.content.ContentResolver
import android.net.Uri
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.repositiry.UriMarkdownFileSource


class ImportFromUriUseCase (private val importFile: ImportFileUseCase) {
    suspend fun execute(uri: Uri, contentResolver: ContentResolver): CachedFile {
        return importFile.execute(UriMarkdownFileSource(uri, contentResolver))
    }
}