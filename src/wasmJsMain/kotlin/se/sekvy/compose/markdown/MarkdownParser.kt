package se.sekvy.compose.markdown

actual class MarkdownParser actual constructor(
    val parserType: ParserType,
) {
    actual fun parse(input: String): NodeType {
        when (parserType) {
            ParserType.Intellij -> {
                return IntellijParser().parse(input)
            }
        }
    }
}

actual enum class ParserType {
    Intellij,
}
