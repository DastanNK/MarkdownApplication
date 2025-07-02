package com.dastan.markdownapplication.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.text.Html
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.dastan.markdownapplication.R
import java.net.URL
import java.util.concurrent.Executors

class UrlImageGetter(private val context: Context) : Html.ImageGetter {

    private val pool = Executors.newCachedThreadPool()

    override fun getDrawable(source: String?): Drawable? {
        if (source.isNullOrBlank()) return null

        val empty = BitmapDrawable(
            context.resources,
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        )

        val lld = LevelListDrawable().apply {
            addLevel(0, 0, empty)
            setBounds(0, 0, 1, 1)
            level = 0
        }

        pool.execute {
            try {
                val bmp  = BitmapFactory.decodeStream(URL(source).openStream())
                val real = BitmapDrawable(context.resources, bmp).apply {
                    setBounds(0, 0, bmp.width, bmp.height)
                }

                (lld.callback as? View)?.post {
                    lld.addLevel(1, 1, real)
                    lld.setBounds(0, 0, bmp.width, bmp.height)
                    lld.level = 1
                }
            } catch (_: Exception) {
                val fallback = AppCompatResources
                    .getDrawable(context, R.drawable.resource_default)
                    ?: empty
                fallback.setBounds(0, 0, fallback.intrinsicWidth, fallback.intrinsicHeight)

                (lld.callback as? View)?.post {
                    lld.addLevel(1, 1, fallback)
                    lld.setBounds(0, 0,
                        fallback.intrinsicWidth, fallback.intrinsicHeight
                    )
                    lld.level = 1
                }
            }
        }

        return lld
    }

}