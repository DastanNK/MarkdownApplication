package com.dastan.markdownapplication.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class EmptyStateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, c: ViewGroup?, s: Bundle?
    ): View = TextView(requireContext()).apply {
        text = "No file yet. Use drawer → Add New File."
        textSize = 18f
        gravity = Gravity.CENTER
    }
}
