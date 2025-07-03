package com.dastan.markdownapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_files")
data class CachedFile(
    @PrimaryKey val id: String = "0",
    val name: String,
    val content: String,
    val lastOpened: Long = 0L
)
