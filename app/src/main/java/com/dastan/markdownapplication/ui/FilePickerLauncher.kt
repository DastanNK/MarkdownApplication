package com.dastan.markdownapplication.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class FilePickerLauncher(
    private val activity: AppCompatActivity,
    private val onFilePicked: (Uri) -> Unit
) {
    private val launcher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            onFilePicked(uri)
        }
    }

    fun launch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
        }
        launcher.launch(intent)
    }
}