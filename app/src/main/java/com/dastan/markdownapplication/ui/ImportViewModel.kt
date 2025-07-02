package com.dastan.markdownapplication.ui

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val importFromUrlUseCase: ImportFromUrlUseCase,
    private val importFromUriUseCase: ImportFromUriUseCase,
) : ViewModel() {
    private val _opened = MutableSharedFlow<CachedFile>(extraBufferCapacity = 1)
    val opened = _opened.asSharedFlow()

    fun fromUri(uri: Uri, cr: ContentResolver) = viewModelScope.launch {
        _opened.tryEmit(importFromUriUseCase.execute(uri, cr))   // файл уже в БД
    }
    fun fromUrl(url: String) = viewModelScope.launch {
        _opened.tryEmit(importFromUrlUseCase.execute(url))
    }
}