package com.dastan.markdownapplication.ui.preview

import androidx.lifecycle.ViewModel
import com.dastan.markdownapplication.data.model.MarkdownBlock
import com.dastan.markdownapplication.domain.usecases.ParseMarkdownUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MarkdownPreviewViewModel @Inject constructor(private val parseMarkdownUseCase: ParseMarkdownUseCase): ViewModel() {
    fun getMarkdownBlocks(markdown: String): List<MarkdownBlock>{
        return parseMarkdownUseCase.execute(markdown)
    }
}