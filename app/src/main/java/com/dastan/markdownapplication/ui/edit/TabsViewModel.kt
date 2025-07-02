package com.dastan.markdownapplication.ui.edit

import androidx.lifecycle.ViewModel
import com.dastan.markdownapplication.data.model.UiTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class TabsViewModel @Inject constructor() : ViewModel() {
    private val _tab = MutableStateFlow(UiTab.EDIT)
    val tab = _tab.asStateFlow()

    fun select(tab: UiTab) { _tab.value = tab }
}