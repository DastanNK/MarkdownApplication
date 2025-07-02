package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.model.Inline
import com.dastan.markdownapplication.data.model.MarkdownBlock


class ParseMarkdownUseCase {
    private val token = Regex(
        """(\*\*(.+?)\*\*|__(.+?)__|\*(.+?)\*|_(.+?)_|~~(.+?)~~)"""
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

            if (line.trim().startsWith("|")) {
                val tableLines = mutableListOf<String>()
                while (index < lines.size && lines[index].trim().startsWith("|")) {
                    tableLines += lines[index]
                    index++
                }
                parseTable(tableLines)?.let(blocks::add)
                continue
            }

            val headingMatch = Regex("""^(#{1,6})\s+(.*)$""").find(line)
            if (headingMatch != null) {
                val level = headingMatch.groupValues[1].length
                val text  = headingMatch.groupValues[2]
                blocks += MarkdownBlock.Heading(level, text)
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

    private fun parseInline(src: String): List<Inline> {
        val list  = mutableListOf<Inline>()
        var last  = 0
        for (m in token.findAll(src)) {
            if (m.range.first > last)
                list.add(Inline.Text(src.substring(last, m.range.first)))
            when {
                m.groupValues[2].isNotEmpty() || m.groupValues[3].isNotEmpty() ->
                    list.add(Inline.Bold(m.groupValues[2] + m.groupValues[3]))
                m.groupValues[4].isNotEmpty() || m.groupValues[5].isNotEmpty() ->
                    list.add(Inline.Italic(m.groupValues[4] + m.groupValues[5]))
                m.groupValues[6].isNotEmpty() ->
                    list.add(Inline.Strike(m.groupValues[6]))
            }
            last = m.range.last + 1
        }
        if (last < src.length) list.add(Inline.Text(src.substring(last)))
        return list
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

