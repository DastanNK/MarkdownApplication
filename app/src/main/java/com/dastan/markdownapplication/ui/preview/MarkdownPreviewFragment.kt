package com.dastan.markdownapplication.ui.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dastan.markdownapplication.domain.usecases.ParseMarkdownUseCase
import com.dastan.markdownapplication.ui.MarkdownRenderer
import com.dastan.markdownapplication.ui.edit.MarkdownEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MarkdownPreviewFragment : Fragment() {

    /** та же VM, что и у Edit-фрагмента */
    private val editVM: MarkdownEditViewModel by activityViewModels()
    private val previewViewModel:MarkdownPreviewViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, c: ViewGroup?, s: Bundle?
    ): View {
        val ctx = requireContext()

        /* контейнер для готовых вью */
        val linear = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        val scroll = ScrollView(ctx).apply { addView(linear) }

        /* Markdown-рендерер (Markwon или свой) */
        val renderer = MarkdownRenderer(ctx, viewLifecycleOwner.lifecycleScope)

        /* подписка на текст файла */
        viewLifecycleOwner.lifecycleScope.launch {
            editVM.current.collect { file ->
                val md = file?.content.orEmpty()
                val blocks = previewViewModel.getMarkdownBlocks(md)
                linear.removeAllViews()                       // очистили
                renderer.render(blocks).forEach(linear::addView)  // перерисовали
            }
        }
        return scroll
    }
}


