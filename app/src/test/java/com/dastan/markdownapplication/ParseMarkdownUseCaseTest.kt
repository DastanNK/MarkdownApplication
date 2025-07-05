package com.dastan.markdownapplication

import com.dastan.markdownapplication.data.model.Inline
import com.dastan.markdownapplication.data.model.MarkdownBlock
import com.dastan.markdownapplication.domain.usecases.ParseMarkdownUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ParseMarkdownUseCaseTest {

    private val parser = ParseMarkdownUseCase()

    @Test
    fun `h1 and h3 parsed as Heading blocks`() {
        val md = "# Title\n### Sub"
        val result = parser.execute(md)

        val expected = listOf(
            MarkdownBlock.Heading(level = 1, content = listOf(Inline.Text("Title"))),
            MarkdownBlock.Heading(level = 3, content = listOf(Inline.Text("Sub")))
        )
        assertEquals(expected, result)
    }

    @Test
    fun `bold italic strike inside paragraph`() {
        val md = "This is **bold**, *italic* and ~~strike~~"
        val result = parser.execute(md).first() as MarkdownBlock.Paragraph

        val segments = result.segments
        assertEquals(6, segments.size)
        assertEquals("This is ", (segments[0] as Inline.Text).text)
        assertEquals("bold",     ((segments[1] as Inline.Bold).content.first() as Inline.Text).text)
        assertEquals(", ",       (segments[2] as Inline.Text).text)
        assertEquals("italic",   ((segments[3] as Inline.Italic).content.first() as Inline.Text).text)
        assertEquals(" and ",    (segments[4] as Inline.Text).text)
        assertEquals("strike",   ((segments[5] as Inline.Strike).content.first() as Inline.Text).text)
    }

    @Test
    fun `image line parsed as Image block`() {
        val md = "![Alt](http://ex.com/i.png)"
        val block = parser.execute(md).single() as MarkdownBlock.Image

        assertEquals("Alt", block.alt)
        assertEquals("http://ex.com/i.png", block.url)
    }

    @Test
    fun `markdown table parsed correctly`() {
        val md = """
            |Name|Age|
            |----|---|
            |Bob |23 |
            |Eva |31 |
        """.trimIndent()

        val tbl = parser.execute(md).single() as MarkdownBlock.Table

        assertEquals(listOf("Name", "Age"), tbl.header)
        assertEquals(listOf(listOf("Bob", "23"), listOf("Eva", "31")), tbl.rows)
    }

    @Test
    fun `single pipe line not parsed as table but as paragraph`() {
        val md = "| Just a pipe line |"
        val blocks = parser.execute(md)

        assertEquals(1, blocks.size)
        val paragraph = blocks.first() as MarkdownBlock.Paragraph
        val actualText = (paragraph.segments.first() as Inline.Text).text
        assertEquals("| Just a pipe line |", actualText)
    }

    @Test
    fun `sequence heading image paragraph`() {
        val md = """
            ## Photo
            ![cat](cat.jpg)
            Cute cat text
        """.trimIndent()

        val blocks = parser.execute(md)
        assertEquals(3, blocks.size)

        assertTrue(blocks[0] is MarkdownBlock.Heading)
        val heading = blocks[0] as MarkdownBlock.Heading
        assertEquals(2, heading.level)
        assertEquals("Photo", (heading.content.first() as Inline.Text).text)

        assertEquals(MarkdownBlock.Image("cat", "cat.jpg"), blocks[1])

        val paragraph = blocks[2] as MarkdownBlock.Paragraph
        assertEquals("Cute cat text", (paragraph.segments.first() as Inline.Text).text)
    }

    @Test
    fun `nested inline styles parsed correctly`() {
        val md = "**~~*text*~~**"
        val block = parser.execute(md).first() as MarkdownBlock.Paragraph

        val outer = block.segments.first() as Inline.Bold
        val middle = outer.content.first() as Inline.Strike
        val inner = middle.content.first() as Inline.Italic
        val text = inner.content.first() as Inline.Text

        assertEquals("text", text.text)
    }
}