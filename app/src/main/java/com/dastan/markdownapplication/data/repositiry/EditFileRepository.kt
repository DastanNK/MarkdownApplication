package com.dastan.markdownapplication.data.repositiry

import com.dastan.markdownapplication.data.local.FileDao

class EditFileRepository(private val fileDao: FileDao) {
    suspend fun updateContent(name: String, text: String) =
        fileDao.updateContent(name, text)
}