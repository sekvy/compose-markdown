package se.sekvy.compose.markdown

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest() {
    @Test
    fun testIntellijParser() {
        val aft = "Hello *world*"
        compareParsers(aft)
    }

    @Test
    fun testIntellijParserHeader() {
        val flavour = CommonMarkFlavourDescriptor()
        val aft = "### Hello"
        compareParsers(aft)
    }

    @Test
    fun testIntellijSETEXT() {
        val aft = "Hello\n---"
        compareParsers(aft)
    }

    @Test
    fun testIntellijThematicBreak() {
        val aft = "Hello\n\n---"
        compareParsers(aft)
    }

    @Test
    fun testIntellijWitheSpace() {
        val aft = "Hello    whitespace"
        compareParsers(aft)
    }

    @Test
    fun testIntellijImage() {
        val aft = "![skiko](skiko.jpeg)"
        compareParsers(aft)
    }

    @Test
    fun testIntellijBulletList() {
        val aft = """
            A bullet list
            * A
            * B
            * C
        """.trimIndent()
        compareParsers(aft)
    }

    @Test
    fun testIntellijUnorderedList() {
        val flavour = CommonMarkFlavourDescriptor()
        val aft = """
            A bullet list
            1. A
            2. B
            3. C
        """.trimIndent()
        val intellijAstNodes = org.intellij.markdown.parser.MarkdownParser(flavour)
            .buildMarkdownTreeFromString(aft)
        val commonMarkAstNodes = MarkdownParser().parse(aft)

        compareParsers(aft)
    }

    @Test
    fun testIntellijCommon() {
        val aft = """
            1. A
            2. B
            3. C
        """.trimIndent()
        val flavour = CommonMarkFlavourDescriptor()
        val node1 = org.intellij.markdown.parser.MarkdownParser(flavour)
            .buildMarkdownTreeFromString(aft)
        val node2 = IntellijParser().parse(aft)
        compareParsers(aft)
    }

    @Test
    fun testIntellijCodeFence() {
        val aft = """
            ```kotlin
            expect class MarkdownParser() {
                fun parse(input : String): NodeType
            }
            ```
        """.trimIndent()

        val intellijAstNodes = org.intellij.markdown.parser.MarkdownParser(CommonMarkFlavourDescriptor())
            .buildMarkdownTreeFromString(aft)
        val commonMarkAstNodes = MarkdownParser().parse(aft)

        compareParsers(aft)
    }

    @Test
    fun testSoftLineBreak() {
        val aft = "[commonmark-java](https://github.com/commonmark/commonmark-java)".trimIndent()
        compareParsers(aft)
    }

    private fun compareParsers(input: String) {
        val intellijParserNode = IntellijParser().parse(input)
        val commonMarkParserNode = MarkdownParser().parse(input)
        assertEquals(commonMarkParserNode.asString(), intellijParserNode.asString())
    }
}
