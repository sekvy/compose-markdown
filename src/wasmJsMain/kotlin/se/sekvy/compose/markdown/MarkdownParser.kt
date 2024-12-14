package se.sekvy.compose.markdown

actual class MarkdownParser actual constructor(
    private val parserType: ParserType,
) {
    actual fun parse(input: String): NodeType {
        when (parserType) {
            ParserType.Intellij -> {
                return IntellijParser().parse(input)
            }
            else -> throw IllegalArgumentException("$parserType not supported")
        }
    }
}
