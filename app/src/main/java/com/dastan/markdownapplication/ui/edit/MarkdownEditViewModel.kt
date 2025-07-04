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
class MarkdownEditViewModel @Inject constructor(private val updateFileUseCase: UpdateFileUseCase) : ViewModel() {
    private val _file = MutableStateFlow<CachedFile?>(null)
    val file: StateFlow<CachedFile?> = _file

    val draft = MutableStateFlow("")

    fun setFile(f: CachedFile) {
        _file.value = f
        draft.value = f.content
    }

    fun updateDraft(text: String) {
        draft.value = text
    }

    fun save() = viewModelScope.launch {
        val cur = _file.value ?: return@launch
        val newText = draft.value
        _file.value = cur.copy(content = newText)
        updateFileUseCase.execute(cur.name, newText)
    }
    fun discardDraft() {
        val cur = _file.value ?: return
        draft.value = cur.content
    }

}