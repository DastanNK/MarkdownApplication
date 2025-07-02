package com.dastan.markdownapplication.domain.usecases

import com.dastan.markdownapplication.data.model.MarkdownBlock

//TODO: что если index++ сделается но там может быть перемешка bold with table or bold with image. Edge cases

class ParseMarkdownUseCase {

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

            if (line.isNotBlank()) {
                blocks += MarkdownBlock.Paragraph(line)
            }
            index++
        }
        return blocks
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

