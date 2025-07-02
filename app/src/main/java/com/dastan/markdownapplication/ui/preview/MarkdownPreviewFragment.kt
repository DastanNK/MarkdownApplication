package com.dastan.markdownapplication.ui.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dastan.markdownapplication.domain.usecases.ParseMarkdownUseCase
import com.dastan.markdownapplication.ui.MarkdownRenderer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MarkdownPreviewFragment : Fragment() {
    private val markdownPreviewViewModel:MarkdownPreviewViewModel by viewModels()

    companion object {
        private const val ARG_TEXT = "markdown_text"

        fun newInstance(text: String): MarkdownPreviewFragment {
            val fragment = MarkdownPreviewFragment()
            val args = Bundle()
            args.putString(ARG_TEXT, text)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val context = requireContext()

        val linear = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }
        val scroll = ScrollView(context).apply { addView(linear) }

        val raw = arguments?.getString(ARG_TEXT) ?: ""
        val blocks = markdownPreviewViewModel.getMarkdownBlocks(raw)

        val renderer = MarkdownRenderer(requireContext(), viewLifecycleOwner.lifecycleScope)
        renderer.render(blocks).forEach(linear::addView)

        return scroll
    }
}


