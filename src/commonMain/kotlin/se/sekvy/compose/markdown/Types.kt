package se.sekvy.compose.markdown

interface NodeType {
    val parent: NodeType?
    val firstChild: NodeType?
    val lastChild: NodeType?
    val prev: NodeType?
    val next: NodeType?
}

@Suppress("PropertyName")
abstract class NodeImpl : NodeType {
    var _parent: NodeType? = null
    var _firstChild: NodeType? = null
    var _lastChild: NodeType? = null
    var _prev: NodeType? = null
    var _next: NodeType? = null

    override val parent: NodeType?
        get() = _parent
    override val firstChild: NodeType?
        get() = _firstChild
    override val lastChild: NodeType?
        get() = _lastChild
    override val prev: NodeType?
        get() = _prev
    override val next: NodeType?
        get() = _next
}

interface Document : NodeType

interface BlockQuote : NodeType

interface Heading : NodeType {
    val level : Int
}

interface ThematicBreak : NodeType

interface Paragraph : NodeType

interface FencedCodeBlock : NodeType {
    val literal : String
}

interface Image : NodeType {
    val destination : String
}

interface ListBlock : NodeType

interface BulletList : ListBlock {
    val bulletMarker : Char
}

interface OrderedList : ListBlock {
    val startNumber : Int
    val delimiter : Char
}

interface Text : NodeType {
    val literal : String
}

interface Emphasis : NodeType

interface StrongEmphasis : NodeType

interface Code : NodeType {
    val literal : String
}

interface HardLineBreak : NodeType

interface Link : NodeType {
    val destination : String
}

interface ListItem : NodeType

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
internal class SoftLineBreakImpl : NodeImpl(), SoftLineBreak
