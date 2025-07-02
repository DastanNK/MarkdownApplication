package com.dastan.markdownapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Menu
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.ui.ImportViewModel
import com.dastan.markdownapplication.ui.edit.MarkdownEditFragment
import com.dastan.markdownapplication.ui.edit.MarkdownEditViewModel
import com.dastan.markdownapplication.ui.preview.MarkdownPreviewFragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val listViewModel: ListViewModel by viewModels()
    private val editViewModel: MarkdownEditViewModel by viewModels()
    private val importVM: ImportViewModel by viewModels()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerMenu: Menu

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>

    private lateinit var tabEdit: TextView
    private lateinit var tabPreview: TextView
    private var addDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO when you are on edit you can click to toolbar however keyboard might be opened
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        tabEdit = findViewById(R.id.tab_edit)
        tabPreview = findViewById(R.id.tab_preview)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        drawerMenu = navigationView.menu

        filePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                importVM.fromUri(uri, contentResolver)
            }
        }

        navigationView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_add_file) {
                showAddDialog()
            } else {
                listViewModel.files.value
                    .firstOrNull { it.name == item.title }
                    ?.let(listViewModel::select)
            }
            drawerLayout.closeDrawers()
            true
        }

        tabEdit.setOnClickListener {
            showEditFragment()
            highlightTab(true)
        }

        tabPreview.setOnClickListener {
            showPreviewFragment()
            highlightTab(false)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            listViewModel.files.collect { updateDrawerMenu(it) }
        }
        lifecycleScope.launchWhenStarted {
            merge(listViewModel.open, importVM.opened).collect { file ->
                editViewModel.setFile(file)
                showEditFragment();
                highlightTab(true)
            }
        }
    }

    private fun updateDrawerMenu(fileList: List<CachedFile>) {
        drawerMenu.clear()
        drawerMenu.add(0, R.id.nav_add_file, 0, "Добавить файл")
        fileList.forEach { f -> drawerMenu.add(f.name) }
    }

    private fun showAddDialog() {
        val options = arrayOf("Из файла", "По ссылке")
        AlertDialog.Builder(this)
            .setTitle("Импорт Markdown")
            .setItems(options) { _, i ->
                if (i == 0) openFilePicker()
                else openUrlDialog()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
        }
        filePickerLauncher.launch(intent)
    }

    private fun openUrlDialog() {
        val input = EditText(this).apply {
            hint = "https://example.com/file.md"
        }

        val container = FrameLayout(this).apply {
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(48, 0, 48, 0)
            layoutParams = params
            addView(input)
        }

        val dialog =AlertDialog.Builder(this)
            .setTitle("Укажите ссылку на Markdown-файл")
            .setView(container)
            .setPositiveButton("Импорт") { _, _ ->
                val url = input.text.toString().trim()
                if (url.isNotEmpty()) {
                    importVM.fromUrl(url)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
    }


    private fun showEditFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, MarkdownEditFragment())
            .commit()
    }

    private fun showPreviewFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, MarkdownPreviewFragment())
            .commit()
    }

    private fun highlightTab(editSelected: Boolean) {
        tabEdit.isSelected = editSelected
        tabPreview.isSelected = !editSelected
    }

    // Optional: fallback if you still need file name for debug
    /*private fun getFileNameFromUri(uri: Uri): String {
        var name = "unnamed.md"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && index != -1) {
                name = it.getString(index)
            }
        }
        return name
    }*/
}

