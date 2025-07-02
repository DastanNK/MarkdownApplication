package com.dastan.markdownapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MarkdownApplication: Application(){
    override fun onCreate() {
        super.onCreate()
    }
}