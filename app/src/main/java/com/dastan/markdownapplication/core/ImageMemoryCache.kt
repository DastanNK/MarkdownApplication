package com.dastan.markdownapplication.core

import android.graphics.Bitmap

class ImageMemoryCache(private val maxSize: Int = 20) {
    private val cache = object : LinkedHashMap<String, Bitmap>(maxSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Bitmap>): Boolean {
            return size > maxSize
        }
    }

    fun get(url: String): Bitmap? = cache[url]

    fun put(url: String, bitmap: Bitmap) {
        cache[url] = bitmap
    }
}