package se.sekvy.compose.markdown

import Node
import Parser

actual class MarkdownParser actual constructor(
    private val parserType: ParserType,
) {
    actual fun parse(input: String) =
        when (parserType) {
            ParserType.CommonMark -> {
                val parser = Parser()
                requireNotNull(parser.parse(input).convert(null))
            }
            ParserType.Intellij -> {
                IntellijParser().parse(input)
            }
        }
}

private fun Node.convert(parent: NodeType?) =
    when (type) {
        "document" -> DocumentImpl()
        "block_quote" -> BlockQuoteImpl()
        "heading" -> HeadingImpl(level = level.toInt())
        "thematic_break" -> ThematicBreakImpl()
        "paragraph" -> ParagraphImpl()
        "code_block" -> FencedCodeBlockImpl(literal = literal ?: "")
        "image" -> ImageImpl(destination = destination ?: "")
        "list" -> {
            when (listType.lowercase()) {
                "bullet" -> {
                    BulletListImpl(bulletMarker = _listData.bulletChar?.toCharArray()?.firstOrNull() ?: '*')
                }
                "ordered" -> {
                    OrderedListImpl(
                        startNumber = listStart.toInt(),
                        delimiter = _listData.delimiter?.toCharArray()?.firstOrNull() ?: '.',
                    )
                }
                else -> null
            }
        }
        "text" -> TextImpl(literal = literal ?: "")
        "emph" -> EmphasisImpl()
        "strong" -> StrongEmphasisImpl()
        "code" -> CodeImpl(literal = literal ?: "")
        "linebreak" -> HardLineBreakImpl()
        "softbreak" -> SoftLineBreakImpl()
        "link" -> LinkImpl(destination = destination ?: "")
        "item" -> ListItemImpl()
        else -> throw IllegalStateException("Unsupported Node $type")
    }?.updateNode(this, parent)

private fun NodeImpl.updateNode(
    node: Node,
    parent: NodeType?,
): NodeType {
    _parent = parent
    _children.addAll(
        node
            .children()
            .map {
                it.convert(this)
            }.filterNotNull()
            .toList(),
    )
    return this
}

private fun Node.children(): Sequence<Node> =
    sequence {
        var current = firstChild
        while (current != null) {
            yield(current)
            current = current.next
        }
    }

actual enum class ParserType {
    CommonMark,
    Intellij,
}
