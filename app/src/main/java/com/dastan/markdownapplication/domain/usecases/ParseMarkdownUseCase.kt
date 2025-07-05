package com.dastan.markdownapplication.domain.usecases

import android.util.Log
import com.dastan.markdownapplication.data.model.Inline
import com.dastan.markdownapplication.data.model.MarkdownBlock


class ParseMarkdownUseCase {
    private val patterns = listOf(
        Regex("""\*\*(.+?)\*\*""") to { inner: List<Inline> -> Inline.Bold(inner) },
        Regex("""__(.+?)__""") to { inner: List<Inline> -> Inline.Bold(inner) },
        Regex("""\*(?!\*)(.+?)\*""") to { inner: List<Inline> -> Inline.Italic(inner) },
        Regex("""_(?!_)(.+?)_""") to { inner: List<Inline> -> Inline.Italic(inner) },
        Regex("""~~(.+?)~~""") to { inner: List<Inline> -> Inline.Strike(inner) },

    )
    fun execute(markdown: String): List<MarkdownBlock> {
        val blocks = mutableListOf<MarkdownBlock>()
        val lines = markdown.lines()
        var index = 0

        while (index < lines.size) {
            val line = lines[index]
            val imageRegex = Regex("""!\[(.*?)\]\((\S+?)(?:\s+"(.*?)")?\)""")
            val match = imageRegex.find(line)
            if (match != null) {
                val alt = match.groupValues[1]
                val url = match.groupValues[2]
                blocks += MarkdownBlock.Image(alt, url)
                index++
                continue
            }

            if (line.trim().startsWith("|") && index + 1 < lines.size) {
                val headerCells = line.trim().split("|").map { it.trim() }
                    .dropWhile { it.isEmpty() }
                    .dropLastWhile { it.isEmpty() }

                val nextLine = lines[index + 1]
                if (isTableSeparator(nextLine, headerCells.size)) {
                    val tableLines = mutableListOf<String>()
                    while (index < lines.size && lines[index].trim().startsWith("|")) {
                        tableLines += lines[index]
                        index++
                    }
                    val table = parseTable(tableLines)
                    if (table != null) {
                        blocks.add(table)
                    } else {
                        val paragraphText = tableLines.joinToString("\n")
                        blocks.add(MarkdownBlock.Paragraph(parseInline(paragraphText)))
                    }
                    continue
                }
            }

            val headingMatch = Regex("""^(#{1,6})\s+(.*)$""").find(line)
            if (headingMatch != null) {
                val level = headingMatch.groupValues[1].length
                val content = headingMatch.groupValues[2]
                blocks += MarkdownBlock.Heading(level, parseInline(content))
                index++
                continue
            }

            if (line.isNotBlank()){
                blocks += MarkdownBlock.Paragraph(parseInline(line))
            }
            index++
        }
        return blocks
    }
    private fun isTableSeparator(line: String, expectedColumnCount: Int): Boolean {
        val parts = line.trim().split("|").map { it.trim() }
        val cells = parts.dropWhile { it.isEmpty() }.dropLastWhile { it.isEmpty() }

        if (cells.size != expectedColumnCount) return false

        return cells.all { it.matches(Regex("^:?-{1,}:?$")) }
    }


    private fun parseInline(text: String): List<Inline> {
        if (text.isEmpty()) return emptyList()

        var earliestMatchStart = Int.MAX_VALUE
        var selectedMatch: MatchResult? = null
        var selectedConstructor: ((List<Inline>) -> Inline)? = null

        for ((regex, constructor) in patterns) {
            val match = regex.find(text)
            if (match != null && match.range.first < earliestMatchStart) {
                earliestMatchStart = match.range.first
                selectedMatch = match
                selectedConstructor = constructor
            }
        }

        if (selectedMatch != null && selectedConstructor != null) {
            val before = text.substring(0, selectedMatch.range.first)
            val matched = selectedMatch.groupValues[1]
            val after = text.substring(selectedMatch.range.last + 1)

            return buildList {
                addAll(parseInline(before))
                add(selectedConstructor(parseInline(matched)))
                addAll(parseInline(after))
            }
        }

        return listOf(Inline.Text(text))
    }

    private fun parseTable(lines: List<String>): MarkdownBlock.Table? {
        if (lines.size < 2) return null

        fun String.cells() =
            trim().removePrefix("|").removeSuffix("|").split("|").map { it.trim() }

        val header = lines.first().cells()
        val rows   = lines.drop(2).map { it.cells() }

        return MarkdownBlock.Table(header, rows)
    }
}

