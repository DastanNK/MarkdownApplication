package com.dastan.markdownapplication.ui.importfile

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportDialogFragment : DialogFragment() {
    //TODO: rewrite it to R.string....
    interface Callback {
        fun onImportFromFile()
        fun onImportFromUrl(url: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val options = arrayOf("Из файла", "По ссылке")
        val ctx = requireContext()
        return AlertDialog.Builder(ctx)
            .setTitle("Импорт Markdown")
            .setItems(options) { _, which ->
                val cb = parentFragment as? Callback ?: activity as Callback
                if (which == 0) {
                    cb.onImportFromFile()
                } else {
                    showUrlDialog(cb)
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

    }

    private fun showUrlDialog(cb: Callback) {
        val input = EditText(requireContext()).apply { hint = "https://example.com/file.md" }
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
            .setTitle("Укажите ссылку на Markdown-файл")
            .setView(container)
            .setPositiveButton("Импорт") { _, _ ->
                val url = input.text.toString().trim()
                if (url.isNotEmpty()) cb.onImportFromUrl(url)
            }
            .setNegativeButton("Отмена", null)
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
    }
}