package se.sekvy.compose.markdown

/**
 * Markdown Parser, parses a Markdown string and outputs a node tree.
 */
expect class MarkdownParser() {
    fun parse(input: String): NodeType
}
