package com.dastan.markdownapplication.data.repositiry

import com.dastan.markdownapplication.data.model.CachedFile
import com.dastan.markdownapplication.data.local.FileDao
import kotlinx.coroutines.flow.Flow

class CachedFileRepository(private val fileDao: FileDao) {
    suspend fun saveFile(id:String, name: String, content: String):Long =
        fileDao.insert(CachedFile(id=id, name = name, content = content, lastOpened = System.currentTimeMillis()))

    fun getAllFiles(): Flow<List<CachedFile>> = fileDao.getAll()

    suspend fun getLastOpened(): CachedFile? = fileDao.getLastOpened()

    suspend fun markOpened(file: CachedFile) =
        fileDao.insert(file.copy(lastOpened = System.currentTimeMillis()))
    suspend fun renameFile(id: String, newName: String) = fileDao.rename(id, newName)
}