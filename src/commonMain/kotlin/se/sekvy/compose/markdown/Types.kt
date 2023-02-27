package se.sekvy.compose.markdown

interface NodeType {
    val parent: NodeType?
    val children: List<NodeType>
}

@Suppress("PropertyName")
internal abstract class NodeImpl : NodeType {
    val _children: MutableList<NodeType> = mutableListOf()
    var _parent: NodeType? = null

    override val parent: NodeType?
        get() = _parent
    override val children: List<NodeType>
        get() = _children
}

interface Document : NodeType

interface BlockQuote : NodeType

interface Heading : NodeType {
    val level: Int
}

interface ThematicBreak : NodeType

interface Paragraph : NodeType

interface FencedCodeBlock : NodeType {
    val literal: String
}

interface Image : NodeType {
    val destination: String
}

interface ListBlock : NodeType

interface BulletList : ListBlock {
    val bulletMarker: Char
}

interface OrderedList : ListBlock {
    val startNumber: Int
    val delimiter: Char
}

interface Text : NodeType {
    val literal: String
}

interface Emphasis : NodeType

interface StrongEmphasis : NodeType

interface Code : NodeType {
    val literal: String
}

interface HardLineBreak : NodeType

interface Link : NodeType {
    val destination: String
}

interface ListItem : NodeType
interface ListItemDelimiter : NodeType {
    val literal: String
}

interface SoftLineBreak : NodeType

internal class DocumentImpl : NodeImpl(), Document
internal class BlockQuoteImpl : NodeImpl(), BlockQuote
internal class HeadingImpl(override val level: Int) : NodeImpl(), Heading
internal class ThematicBreakImpl : NodeImpl(), ThematicBreak
internal class ParagraphImpl : NodeImpl(), Paragraph
internal class FencedCodeBlockImpl(override val literal: String) : NodeImpl(), FencedCodeBlock
internal class ImageImpl(override val destination: String) : NodeImpl(), Image
internal class BulletListImpl(override val bulletMarker: Char) : NodeImpl(), BulletList
internal class OrderedListImpl(override val startNumber: Int, override val delimiter: Char) : NodeImpl(), OrderedList
internal class TextImpl(override val literal: String) : NodeImpl(), Text
internal class EmphasisImpl : NodeImpl(), Emphasis
internal class StrongEmphasisImpl : NodeImpl(), StrongEmphasis
internal class CodeImpl(override val literal: String) : NodeImpl(), Code
internal class HardLineBreakImpl : NodeImpl(), HardLineBreak
internal class LinkImpl(override val destination: String) : NodeImpl(), Link
internal class ListItemImpl : NodeImpl(), ListItem
internal class ListItemDelimiterImpl(override val literal: String) : NodeImpl(), ListItemDelimiter
internal class SoftLineBreakImpl : NodeImpl(), SoftLineBreak

fun NodeType.asString(indent: String = ""): String {
    val out = this::class.simpleName ?: ""
    return out + "\n$indent" + children.joinToString("\n$indent") {
        it.asString("$indent ")
    }
}
