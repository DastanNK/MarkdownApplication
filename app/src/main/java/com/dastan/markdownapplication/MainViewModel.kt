package com.dastan.markdownapplication

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAll: GetAllFilesUseCase,
    private val getLast: GetLastOpenedFileUseCase,
    private val markOpened: MarkFileOpenedUseCase,
    private val importFromUrlUseCase: ImportFromUrlUseCase,
    private val importFromUriUseCase: ImportFromUriUseCase,

    //private val addFileUseCase:AddFileUseCase
) : ViewModel() {
    //TODO: make it private with StateFlow
    val files = MutableStateFlow<List<CachedFile>>(emptyList())
    val current = MutableStateFlow<CachedFile?>(null)

    init { refresh() }

    private fun refresh() = viewModelScope.launch {
        files.value = getAll.execute()
        current.value = current.value ?: getLast.execute()
    }

    fun importFromUri(uri: Uri, cr: ContentResolver) = viewModelScope.launch {
        current.value = importFromUriUseCase.execute(uri, cr)
        refresh()
    }

    fun importFromUrl(url: String) = viewModelScope.launch {
        current.value = importFromUrlUseCase.execute(url)
        refresh()
    }
    /*private suspend fun saveFileToDatabase(){
        addFileUseCase.execute(current.value?.name?:"", current.value?.content?:"")
    }*/

    fun select(file: CachedFile) = viewModelScope.launch {
        markOpened.execute(file)
        current.value = file.copy(lastOpened = System.currentTimeMillis())
        refresh()
    }
    fun selectByName(name: String) = viewModelScope.launch {
        val file = files.value.firstOrNull { it.name == name } ?: return@launch
        select(file)
    }
    fun getContentByName(name: String): String {
        return files.value.firstOrNull { it.name == name }?.content.orEmpty()
    }

}