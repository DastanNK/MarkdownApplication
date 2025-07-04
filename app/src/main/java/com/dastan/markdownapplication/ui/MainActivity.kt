package com.dastan.markdownapplication.ui

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.dastan.markdownapplication.R
import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.model.UiTab
import com.dastan.markdownapplication.ui.importfile.FilePickerLauncher
import com.dastan.markdownapplication.ui.importfile.ImportDialogFragment
import com.dastan.markdownapplication.ui.importfile.ImportViewModel
import com.dastan.markdownapplication.ui.toolbar.ToolbarController
import com.dastan.markdownapplication.ui.edit.MarkdownEditFragment
import com.dastan.markdownapplication.ui.edit.MarkdownEditViewModel
import com.dastan.markdownapplication.ui.edit.TabsViewModel
import com.dastan.markdownapplication.ui.menu.ListViewModel
import com.dastan.markdownapplication.ui.preview.MarkdownPreviewFragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.merge

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ImportDialogFragment.Callback {
    private val tabsVM: TabsViewModel by viewModels()
    private val listViewModel: ListViewModel by viewModels()
    private val editViewModel: MarkdownEditViewModel by viewModels()
    private val importVM: ImportViewModel by viewModels()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawerMenu: Menu
    private lateinit var tabEdit: TextView
    private lateinit var tabPreview: TextView

    private lateinit var toolbarController: ToolbarController
    private lateinit var filePickerLauncher: FilePickerLauncher

    private var dialogShown = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        tabEdit = findViewById(R.id.tab_edit)
        tabPreview = findViewById(R.id.tab_preview)
        drawerMenu = navigationView.menu

        toolbarController = ToolbarController(this, drawerLayout, toolbar).also { it.init() }
        filePickerLauncher = FilePickerLauncher(this) { uri ->
            importVM.fromUri(uri, contentResolver)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_add_file) {
                ImportDialogFragment()
                    .show(supportFragmentManager, "import_dialog")
            } else {
                listViewModel.files.value
                    .firstOrNull { it.name == item.title }
                    ?.let(listViewModel::select)
            }
            drawerLayout.closeDrawers()
            true
        }
        tabEdit.setTextColor(ContextCompat.getColorStateList(this, R.color.my_button_text))


        tabPreview.setTextColor(ContextCompat.getColorStateList(this, R.color.my_button_text))
        tabEdit.setOnClickListener {
            highlightTab(true)
            showEditFragment()
            tabsVM.select(UiTab.EDIT)
        }
        tabPreview.setOnClickListener {
            highlightTab(false)
            showPreviewFragment()
            tabsVM.select(UiTab.PREVIEW)
        }

        observeViewModel()
        observeTabs()
        openDialogFirstTime(savedInstanceState)
    }


    override fun onImportFromFile() {
        filePickerLauncher.launch()
    }

    override fun onImportFromUrl(url: String) {
        importVM.fromUrl(url)
    }
    private fun highlightTab(editSelected: Boolean) {
        tabEdit.isSelected = editSelected
        tabPreview.isSelected = !editSelected
    }
    private fun openDialogFirstTime(savedInstanceState: Bundle?){
        if (savedInstanceState == null && !dialogShown) {
            dialogShown = true
            ImportDialogFragment().show(supportFragmentManager, "import_dialog")
        }
    }
    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            listViewModel.files.collect { files ->
                updateDrawerMenu(files)
            }
        }
        lifecycleScope.launchWhenStarted {
            merge(listViewModel.open, importVM.opened).collect { file ->
                editViewModel.setFile(file)
                showEditFragment()
                tabsVM.select(UiTab.EDIT)
            }
        }
    }

    private fun updateDrawerMenu(fileList: List<CachedFile>) {
        drawerMenu.clear()
        drawerMenu.add(0, R.id.nav_add_file, 0, R.string.add_file)
        fileList.forEach { file -> drawerMenu.add(file.name) }
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

    private fun observeTabs() = lifecycleScope.launchWhenStarted {
        tabsVM.tab.collect { tab -> highlightTab(tab == UiTab.EDIT) }
    }


}

