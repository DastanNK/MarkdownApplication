package com.dastan.markdownapplication

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
    private val getAll: GetAllFilesUseCase,
    //private val getLast: GetLastOpenedFileUseCase,
    private val markOpened: MarkFileOpenedUseCase,
    //private val importFromUrlUseCase: ImportFromUrlUseCase,
    //private val importFromUriUseCase: ImportFromUriUseCase,

    //private val addFileUseCase:AddFileUseCase
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