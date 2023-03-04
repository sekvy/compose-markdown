package se.sekvy.compose.markdown

import org.commonmark.node.BlockQuote
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.Document
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.node.ThematicBreak
import org.commonmark.parser.Parser
import java.lang.IllegalStateException

actual class MarkdownParser actual constructor(val parserType: ParserType) {
    actual fun parse(input: String): NodeType {
        when (parserType) {
            ParserType.CommonMark -> {
                val parser = Parser.builder().build()
                val documentNode = parser.parse(input)
                return requireNotNull(documentNode.convert(parent = null))
            }
            ParserType.Intellij -> {
                return IntellijParser().parse(input)
            }
        }
    }
}

actual enum class ParserType {
    CommonMark, Intellij
}

/**
 * Recursive traversal of the node tree to convert it to a common format.
 */
private fun Node.convert(parent: NodeType?) =
    when (this) {
        is Document -> DocumentImpl()
        is BlockQuote -> BlockQuoteImpl()
        is Heading -> HeadingImpl(level = level)
        is ThematicBreak -> ThematicBreakImpl()
        is Paragraph -> ParagraphImpl()
        is FencedCodeBlock -> FencedCodeBlockImpl(literal = literal)
        is Image -> ImageImpl(destination = destination)
        is BulletList -> BulletListImpl(bulletMarker = bulletMarker)
        is OrderedList -> OrderedListImpl(startNumber = startNumber, delimiter = delimiter)
        is Text -> TextImpl(literal = literal)
        is Emphasis -> EmphasisImpl()
        is StrongEmphasis -> StrongEmphasisImpl()
        is Code -> CodeImpl(literal = literal)
        is HardLineBreak -> HardLineBreakImpl()
        is Link -> LinkImpl(destination = destination)
        is ListItem -> ListItemImpl()
        is SoftLineBreak -> SoftLineBreakImpl()
        else -> throw IllegalStateException("Unsupported node ${this.javaClass.simpleName}")
    }.updateNode(this, parent)

private fun NodeImpl.updateNode(node: Node, parent: NodeType?): NodeType {
    _parent = parent
    _children.addAll(
        node.children().map {
            it.convert(this)
        }.toList()
    )
    return this
}

private fun Node.children(): Sequence<Node> {
    return sequence {
        var current = firstChild
        while (current != null) {
            yield(current)
            current = current.next
        }
    }
}
