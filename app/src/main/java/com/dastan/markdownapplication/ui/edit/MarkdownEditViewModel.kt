package com.dastan.markdownapplication.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.domain.usecases.UpdateFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarkdownEditViewModel @Inject constructor(private val updateFileUseCase: UpdateFileUseCase): ViewModel()  {
    private val _current = MutableStateFlow<CachedFile?>(null)
    val current = _current.asStateFlow()

    fun setFile(f: CachedFile) { _current.value = f }

    fun updateContent(text: String) = viewModelScope.launch {
        _current.value?.let { f ->
            _current.value = f.copy(content = text)
            updateFileUseCase.execute(f.name, text)                 // Room UPDATE
        }
    }

}