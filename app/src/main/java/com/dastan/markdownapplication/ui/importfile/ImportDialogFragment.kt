package com.dastan.markdownapplication.ui.importfile

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.dastan.markdownapplication.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportDialogFragment : DialogFragment() {
    interface Callback {
        fun onImportFromFile()
        fun onImportFromUrl(url: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = requireContext()
        val options =arrayOf(
            ctx.getString(R.string.from_file),
            ctx.getString(R.string.from_url)
        )
        return AlertDialog.Builder(ctx)
            .setTitle(R.string.import_markdown)
            .setItems(options) { _, which ->
                val cb = parentFragment as? Callback ?: activity as Callback
                if (which == 0) {
                    cb.onImportFromFile()
                } else {
                    showUrlDialog(cb)
                }
            }
            .create()

    }

    private fun showUrlDialog(cb: Callback) {
        val input = EditText(requireContext()).apply { hint = getString(R.string.url_hint)}
        val container = FrameLayout(requireContext()).apply {
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(48, 0, 48, 0)
            layoutParams = params
            addView(input)
        }
        val dialog =AlertDialog.Builder(requireContext())
            .setTitle(R.string.enter_url_title)
            .setView(container)
            .setPositiveButton(R.string.import_name) { _, _ ->
                val url = input.text.toString().trim()
                if (url.isNotEmpty()) cb.onImportFromUrl(url)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
    }
}