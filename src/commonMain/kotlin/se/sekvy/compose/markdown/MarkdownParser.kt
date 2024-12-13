package se.sekvy.compose.markdown

/**
 * Markdown Parser, parses a Markdown string and outputs a node tree.
 */
expect class MarkdownParser(
    parserType: ParserType,
) {
    fun parse(input: String): NodeType
}

expect enum class ParserType
