package com.dastan.markdownapplication.ui.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.dastan.markdownapplication.R
import com.dastan.markdownapplication.ui.renderer.MarkdownRenderer
import com.dastan.markdownapplication.ui.edit.MarkdownEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MarkdownPreviewFragment : Fragment() {

    private val editVM: MarkdownEditViewModel by activityViewModels()
    private val previewViewModel:MarkdownPreviewViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, c: ViewGroup?, s: Bundle?
    ): View {
        val ctx = requireContext()

        val linear = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(ctx, R.color.my_background))

        }
        val scroll = ScrollView(ctx).apply { addView(linear) }

        val renderer = MarkdownRenderer(ctx, viewLifecycleOwner.lifecycleScope)

        viewLifecycleOwner.lifecycleScope.launch {
            editVM.file.collect { file ->
                val md = file?.content.orEmpty()
                val blocks = previewViewModel.getMarkdownBlocks(md)
                linear.removeAllViews()
                renderer.render(blocks).forEach(linear::addView)
            }
        }
        return scroll
    }

}


