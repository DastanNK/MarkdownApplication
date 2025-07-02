package com.dastan.markdownapplication.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import com.dastan.markdownapplication.R
import com.dastan.markdownapplication.data.model.MarkdownBlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class MarkdownRenderer(
    private val context: Context,
    private val scope: CoroutineScope
) {

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
            text = h.text
            textSize = when (h.level) {
                1 -> 24f; 2 -> 22f; 3 -> 20f; 4 -> 18f; 5 -> 16f else -> 14f
            }
            setTypeface(null, Typeface.BOLD)
        }

    private fun renderParagraph(p: MarkdownBlock.Paragraph): View =
        TextView(context).apply {
            text = applyInlineStyles(p.text)
            textSize = 14f
        }

    private fun renderTable(t: MarkdownBlock.Table): View {
        val table = TableLayout(context)

        table.addView(buildRow(t.header, isHeader = true))
        t.rows.forEach { table.addView(buildRow(it, isHeader = false)) }

        return HorizontalScrollView(context).apply {
            addView(table)
        }
    }

    private fun buildRow(cells: List<String>, isHeader: Boolean): TableRow =
        TableRow(context).apply {
            cells.forEachIndexed { i, cell ->
                addView(TextView(context).apply {
                    setPadding(16, 8, 16, 8)
                    text  = applyInlineStyles(cell)
                    if (isHeader) setTypeface(null, Typeface.BOLD)
                    if (!isHeader && i == 0) typeface = Typeface.MONOSPACE

                    background = AppCompatResources.getDrawable(context, R.drawable.table_cell)
                })
            }
        }


    private fun applyInlineStyles(text: String): CharSequence {
        var src = text
        src = src.replace(Regex("""\*\*(.+?)\*\*|__(.+?)__"""), "<b>${'$'}1${'$'}2</b>")
        src = src.replace(
            Regex("""(?<!\*)\*(?!\*)(.+?)\*(?!\*)|_(.+?)_"""),
            "<i>${'$'}1${'$'}2</i>"
        )
        src = src.replace(Regex("""~~(.+?)~~"""), "<s>$1</s>")
        src = src.replace(Regex("""!\[(.*?)\]\((.*?)\)"""), "<img src=\"$2\" alt=\"$1\"/>")

        return Html.fromHtml(
            src,
            Html.FROM_HTML_MODE_LEGACY,
            UrlImageGetter(context),
            null
        )
    }
    private fun renderImage(img: MarkdownBlock.Image): View {
        val iv = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
            contentDescription = img.alt
        }

        scope.launch {
            val bmp = withContext(Dispatchers.IO) {
                runCatching {
                    URL(img.url).openStream().use { BitmapFactory.decodeStream(it) }
                }.getOrNull()
            }
            bmp?.let { iv.setImageBitmap(it) }
        }

        return iv
    }


}