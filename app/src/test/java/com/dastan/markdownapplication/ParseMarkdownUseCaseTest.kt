package com.dastan.markdownapplication

import com.dastan.markdownapplication.data.model.Inline
import com.dastan.markdownapplication.data.model.MarkdownBlock
import com.dastan.markdownapplication.domain.usecases.ParseMarkdownUseCase
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ParseMarkdownUseCaseTest {

    private val parser = ParseMarkdownUseCase()

    @Test
    fun `h1 and h3 parsed as Heading blocks`() {
        val md = "# Title\n### Sub"
        val result = parser.execute(md)

        val expected = listOf(
            MarkdownBlock.Heading(level = 1, text = "Title"),
            MarkdownBlock.Heading(level = 3, text = "Sub")
        )
        assertEquals(expected, result)
    }

    @Test
    fun `bold italic strike inside paragraph`() {
        val md = "This is **bold**, *italic* and ~~strike~~"
        val result = parser.execute(md).first() as MarkdownBlock.Paragraph

        val segments = result.segments
        assertEquals(6, segments.size)
        assertEquals("This is ", (segments[0] as Inline.Text).value)
        assertEquals("bold",     (segments[1] as Inline.Bold).value)
        assertEquals(", ",       (segments[2] as Inline.Text).value)
        assertEquals("italic",   (segments[3] as Inline.Italic).value)
        assertEquals(" and ",    (segments[4] as Inline.Text).value)
        assertEquals("strike",   (segments[5] as Inline.Strike).value)
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
    fun `sequence heading image paragraph`() {
        val md = """
            ## Photo
            ![cat](cat.jpg)
            Cute cat text
        """.trimIndent()

        val blocks = parser.execute(md)
        assertEquals(3, blocks.size)
        assertEquals(MarkdownBlock.Heading(2, "Photo"), blocks[0])
        assertEquals(MarkdownBlock.Image("cat", "cat.jpg"), blocks[1])
        assertEquals(
            MarkdownBlock.Paragraph(listOf(Inline.Text("Cute cat text"))),
            blocks[2]
        )
    }
}