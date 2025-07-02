package com.dastan.markdownapplication.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.dastan.markdownapplication.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MarkdownEditFragment : Fragment() {

    private val vm: MarkdownEditViewModel by activityViewModels()
    private lateinit var edit: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        edit = EditText(requireContext()).apply {
            textSize = 16f
            setPadding(32, 32, 32, 32)

            doOnTextChanged { txt, _, _, _ ->
                vm.updateContent(txt.toString())
            }
        }
        return edit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.current
                .map { it?.content.orEmpty() }
                .distinctUntilChanged()
                .collect { newText ->
                    if (newText == edit.text.toString()) return@collect

                    val cursorPos = edit.selectionStart.coerceAtLeast(0)
                    edit.setText(newText)
                    edit.setSelection(cursorPos.coerceAtMost(newText.length))
                }
        }
    }
}