package com.dastan.markdownapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dastan.markdownapplication.data.model.CachedFile

@Database(
    entities = [CachedFile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
}
