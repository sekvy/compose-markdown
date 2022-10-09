package se.sekvy.compose.markdown

import kotlin.test.Test
import kotlin.test.assertIs

class ParserTest() {
    @Test
    fun testCommonParser() {
        val parser = MarkdownParser()
        val node = parser.parse("Hello *world*")
        assertIs<Document>(node)
        assertIs<Paragraph>(node.firstChild)
        assertIs<Text>(node.firstChild?.firstChild)
        assertIs<Emphasis>(node.firstChild?.firstChild?.next)
        assertIs<Text>(node.firstChild?.firstChild?.next?.firstChild)
    }
}
