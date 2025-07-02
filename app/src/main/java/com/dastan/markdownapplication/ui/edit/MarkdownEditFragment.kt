package com.dastan.markdownapplication.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment


class MarkdownEditFragment : Fragment() {

    companion object {
        private const val ARG_TEXT = "text"

        fun newInstance(text: String): MarkdownEditFragment {
            val fragment = MarkdownEditFragment()
            fragment.arguments = Bundle().apply { putString(ARG_TEXT, text) }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val editText = EditText(requireContext()).apply {
            setText(arguments?.getString(ARG_TEXT) ?: "")
            setPadding(32, 32, 32, 32)
            textSize = 16f
        }
        return editText
    }

}
