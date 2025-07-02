package com.dastan.markdownapplication.ui.toolbar


import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.dastan.markdownapplication.R

class ToolbarController(
    private val activity: AppCompatActivity,
    private val drawerLayout: DrawerLayout,
    private val toolbar: Toolbar
) {
    fun init() {
        activity.setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
}
