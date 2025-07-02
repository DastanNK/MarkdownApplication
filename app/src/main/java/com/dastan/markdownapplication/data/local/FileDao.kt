package com.dastan.markdownapplication.data.local

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import com.dastan.markdownapplication.data.model.CachedFile
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(file: CachedFile): Long

    @Query("SELECT * FROM cached_files WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): CachedFile?

    @Query("SELECT * FROM cached_files ORDER BY lastOpened DESC, id DESC")
    fun getAll(): Flow<List<CachedFile>>

    @Query("SELECT * FROM cached_files ORDER BY lastOpened DESC LIMIT 1")
    suspend fun getLastOpened(): CachedFile?

    @Delete
    suspend fun delete(file: CachedFile)

    @Query("UPDATE cached_files SET content = :content WHERE name = :name")
    suspend fun updateContent(name: String, content: String)

    @Query("UPDATE cached_files SET name = :newName WHERE id = :id")
    suspend fun rename(id: Long, newName: String)
}