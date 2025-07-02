package com.dastan.markdownapplication.data.repositiry

import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.local.FileDao

class CachedFileRepository(private val fileDao: FileDao) {
    suspend fun saveFile(name: String, content: String):Long =
        fileDao.insert(CachedFile(name = name, content = content, lastOpened = System.currentTimeMillis()))

    suspend fun getAllFiles(): List<CachedFile> = fileDao.getAll()

    suspend fun getLastOpened(): CachedFile? = fileDao.getLastOpened()

    suspend fun markOpened(file: CachedFile) =
        fileDao.insert(file.copy(lastOpened = System.currentTimeMillis()))
    suspend fun renameFile(id: Long, newName: String) = fileDao.rename(id, newName)
}