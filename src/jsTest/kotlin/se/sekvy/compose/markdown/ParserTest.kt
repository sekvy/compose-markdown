package se.sekvy.compose.markdown

import Parser
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest() {
    @Test
    fun testJsParser() {
        val parser = Parser()
        val node = parser.parse("Hello *world*")
        val walker = node.walker()
        assertEquals(expected = "document", actual = walker.next().node.type)
        assertEquals(expected = "paragraph", actual = walker.next().node.type)
        assertEquals(expected = "text", actual = walker.next().node.type)
        assertEquals(expected = "emph", actual = walker.next().node.type)
        assertEquals(expected = "text", actual = walker.next().node.type)
        assertEquals(expected = "emph", actual = walker.next().node.type)
        assertEquals(expected = "paragraph", actual = walker.next().node.type)
        assertEquals(expected = "document", actual = walker.next().node.type)
    }
}
