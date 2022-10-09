package se.sekvy.compose.markdown

import Node
import Parser

actual class MarkdownParser actual constructor() {
    actual fun parse(input: String): NodeType {
        val parser = Parser()
        return requireNotNull(parser.parse(input).convert(null, null))
    }
}

fun Node.convert(parent: NodeType?, prev: NodeType?) = when (type) {
    "document" -> DocumentImpl()
    "block_quote" -> BlockQuoteImpl()
    "heading" -> HeadingImpl(level = level.toInt())
    "thematic_break" -> ThematicBreakImpl()
    "paragraph" -> ParagraphImpl()
    "code_block" -> FencedCodeBlockImpl(literal = literal ?: "")
    "image" -> ImageImpl(destination = destination ?: "")
    "list" -> {
        when (listType) {
            "bullet" -> {
                BulletListImpl(bulletMarker = _listData.bulletChar?.toCharArray()?.firstOrNull() ?: '*')
            }
            "ordered" -> {
                OrderedListImpl(
                    startNumber = listStart.toInt(),
                    delimiter = _listData.delimiter?.toCharArray()?.firstOrNull() ?: '.'
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
}?.updateNode(this, parent, prev)

internal fun NodeImpl.updateNode(node: Node, parent: NodeType?, prev: NodeType?): NodeType {
    _parent = parent
    _firstChild = node.firstChild?.convert(parent = this, prev = null)
    _lastChild = _firstChild

    var current = _firstChild?.next
    while (current != null) {
        _lastChild = current
        current = current.next
    }

    _next = node.next?.convert(parent = parent, prev = this)
    _prev = prev
    return this
}
