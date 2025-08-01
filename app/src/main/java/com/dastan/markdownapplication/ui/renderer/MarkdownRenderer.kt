package com.dastan.markdownapplication.ui.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.dastan.markdownapplication.R
import com.dastan.markdownapplication.core.ImageMemoryCache
import com.dastan.markdownapplication.data.model.Inline
import com.dastan.markdownapplication.data.model.MarkdownBlock
import com.dastan.markdownapplication.domain.usecases.ParseMarkdownUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class MarkdownRenderer(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val imageCache = ImageMemoryCache(maxSize = 50)
    fun render(blocks: List<MarkdownBlock>): List<View> =
        blocks.map { block ->
            when (block) {
                is MarkdownBlock.Heading   -> renderHeading(block)
                is MarkdownBlock.Paragraph -> renderParagraph(block)
                is MarkdownBlock.Table     -> renderTable(block)
                is MarkdownBlock.Image     -> renderImage(block)
            }
        }

    private fun renderHeading(h: MarkdownBlock.Heading): View =
        TextView(context).apply {
            text = buildSpannable(h.content)
            textSize = when (h.level) {
                1 -> 24f; 2 -> 22f; 3 -> 20f; 4 -> 18f; 5 -> 16f else -> 14f
            }
            setTypeface(null, Typeface.BOLD)
        }

    private fun renderParagraph(p: MarkdownBlock.Paragraph): View =
        TextView(context).apply {
            text = buildSpannable(p.segments)
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, R.color.my_text_color)) // 💡 Важно

        }

    private fun renderTable(t: MarkdownBlock.Table): View {
        val table = TableLayout(context)

        table.addView(buildRow(t.header, isHeader = true))
        t.rows.forEach { table.addView(buildRow(it, isHeader = false)) }

        return HorizontalScrollView(context).apply {
            addView(table)
        }
    }

    private fun buildRow(cells: List<String>, isHeader: Boolean) =
        TableRow(context).apply {
            cells.forEachIndexed { i, cell ->
                addView(TextView(context).apply {
                    setPadding(16, 8, 16, 8)
                    text = buildSpannable(ParseMarkdownUseCase().execute(cell).let {
                        if (it.isNotEmpty() && it[0] is MarkdownBlock.Paragraph)
                            (it[0] as MarkdownBlock.Paragraph).segments
                        else listOf(Inline.Text(cell))
                    })
                    if (isHeader) setTypeface(null, Typeface.BOLD)
                    if (!isHeader && i == 0) typeface = Typeface.MONOSPACE
                    background = AppCompatResources.getDrawable(context, R.drawable.table_cell)
                })
            }
        }



    private fun renderImage(img: MarkdownBlock.Image): View {
        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
            contentDescription = img.alt
        }

        val cachedBitmap = imageCache.get(img.url)
        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap)
        } else {
            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    runCatching {
                        URL(img.url).openStream().use { BitmapFactory.decodeStream(it) }
                    }.getOrNull()
                }
                bitmap?.let {
                    imageCache.put(img.url, it)
                    imageView.setImageBitmap(it)
                }
            }
        }

        return imageView
    }
    private fun buildSpannable(parts: List<Inline>): Spanned {
        val builder = SpannableStringBuilder()
        appendInlines(builder, parts)
        return builder
    }

    private fun appendInlines(builder: SpannableStringBuilder, inlines: List<Inline>) {
        for (inline in inlines) {
            val start = builder.length
            when (inline) {
                is Inline.Text -> builder.append(inline.text)
                is Inline.Bold -> {
                    appendInlines(builder, inline.content)
                    builder.setSpan(StyleSpan(Typeface.BOLD), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                is Inline.Italic -> {
                    appendInlines(builder, inline.content)
                    builder.setSpan(StyleSpan(Typeface.ITALIC), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                is Inline.Strike -> {
                    appendInlines(builder, inline.content)
                    builder.setSpan(StrikethroughSpan(), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }


}