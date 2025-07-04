package com.dastan.markdownapplication.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ctx = requireContext()
        val root = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32)
            setBackgroundColor(ContextCompat.getColor(ctx, R.color.my_background))

        }

        edit = EditText(ctx).apply {
            textSize = 16f
            setTextColor(ContextCompat.getColor(ctx, R.color.my_text_color))
            setBackgroundColor(ContextCompat.getColor(ctx, R.color.my_edit_background))
        }
        btnSave = Button(ctx).apply {
            text = getString(R.string.save)
            setTextColor(ContextCompat.getColor(ctx, R.color.my_button_text))
            setBackgroundColor(ContextCompat.getColor(ctx, R.color.my_button_background))
        }

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
        edit.doAfterTextChanged { vm.updateDraft(it.toString()) }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.draft.collect { txt ->
                if (txt != edit.text.toString()) {
                    val pos = edit.selectionStart.coerceAtLeast(0)
                    edit.setText(txt)
                    edit.setSelection(pos.coerceAtMost(txt.length))
                }
            }
        }

        btnSave.setOnClickListener {
            vm.save()
            tabsVM.select(UiTab.PREVIEW)
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, MarkdownPreviewFragment())
                .commit()
        }
    }
    override fun onDestroy() {
        super.onDestroy()

        val reallyGoingAway = isRemoving && !requireActivity().isChangingConfigurations

        if (reallyGoingAway) {
            vm.discardDraft()
        }
    }
}
