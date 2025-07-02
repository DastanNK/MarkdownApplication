package com.dastan.markdownapplication.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.dastan.markdownapplication.MainActivity
import com.dastan.markdownapplication.R
import com.dastan.markdownapplication.data.model.UiTab
import com.dastan.markdownapplication.ui.preview.MarkdownPreviewFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MarkdownEditFragment : Fragment() {

    private val vm: MarkdownEditViewModel by activityViewModels()
    private val tabsVM: TabsViewModel by activityViewModels()
    private lateinit var edit: EditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ctx = requireContext()
        val root = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32)
        }

        edit = EditText(ctx).apply { textSize = 16f }
        btnSave = Button(ctx).apply { text = "Сохранить" }

        root.addView(
            edit,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
        root.addView(
            btnSave,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.current
                .map { it?.content.orEmpty() }
                .distinctUntilChanged()
                .collect { newText ->
                    if (newText != edit.text.toString()) {
                        val pos = edit.selectionStart.coerceAtLeast(0)
                        edit.setText(newText)
                        edit.setSelection(pos.coerceAtMost(newText.length))
                    }
                }
        }

        btnSave.setOnClickListener {
            vm.updateContent(edit.text.toString())
            tabsVM.select(UiTab.PREVIEW)
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, MarkdownPreviewFragment())
                .commit()
        }
    }
}
