package com.dastan.markdownapplication.ui.toolbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    getAll: GetAllFilesUseCase,
    private val markOpened: MarkFileOpenedUseCase
) : ViewModel() {
    val files: StateFlow<List<CachedFile>> =
        getAll.execute()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _open = MutableSharedFlow<CachedFile>(extraBufferCapacity = 1)
    val open = _open.asSharedFlow()

    fun select(file: CachedFile) = viewModelScope.launch {
        markOpened.execute(file)
        _open.tryEmit(file)
    }

}