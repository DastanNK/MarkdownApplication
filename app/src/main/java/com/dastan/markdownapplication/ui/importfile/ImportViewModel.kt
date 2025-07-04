package com.dastan.markdownapplication.ui.importfile

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val importFromUrlUseCase: ImportFromUrlUseCase,
    private val importFromUriUseCase: ImportFromUriUseCase,
) : ViewModel() {
    sealed interface UiEvent {
        data class FileOpened(val file: CachedFile) : UiEvent
        data class Error(val message: String)       : UiEvent
    }
    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events

    fun fromUri(uri: Uri, cr: ContentResolver) = viewModelScope.launch {
        try {
            val file = importFromUriUseCase.execute(uri, cr)
            _events.emit(UiEvent.FileOpened(file))
        } catch (e: Exception) {
            _events.emit(UiEvent.Error("Не удалось открыть файл: ${e.localizedMessage}"))
        }
    }

    fun fromUrl(url: String) = viewModelScope.launch {
        try {
            val file = importFromUrlUseCase.execute(url)
            _events.emit(UiEvent.FileOpened(file))
        } catch (e: Exception) {
            _events.emit(UiEvent.Error("Не удалось загрузить: ${e.localizedMessage}"))
        }
    }
}