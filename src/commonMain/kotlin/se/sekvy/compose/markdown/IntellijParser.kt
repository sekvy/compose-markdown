package se.sekvy.compose.markdown

import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor

class IntellijParser {
    fun parse(input: String): NodeType {
        val flavour = CommonMarkFlavourDescriptor()
        val node = org.intellij.markdown.parser.MarkdownParser(flavour)
            .buildMarkdownTreeFromString(input)
        return requireNotNull(node.convert(input, null))
    }
}

/**
 * Recursive traversal of the node tree to convert it to a common format.
 */
private fun ASTNode.convert(input: String, parent: NodeType?): NodeType? {
    var skipChildren = false
    return when (type) {
        MarkdownElementTypes.MARKDOWN_FILE -> DocumentImpl()
        MarkdownElementTypes.SETEXT_1,
        MarkdownElementTypes.SETEXT_2 -> {
            if (parent !is Heading) {
                HeadingImpl(level = type.name.last().digitToInt())
            } else {
                null // This is the actual --- or ====. Ignore.
            }
        }
        MarkdownTokenTypes.ATX_HEADER -> null
        MarkdownElementTypes.ATX_1,
        MarkdownElementTypes.ATX_2,
        MarkdownElementTypes.ATX_3,
        MarkdownElementTypes.ATX_4,
        MarkdownElementTypes.ATX_5,
        MarkdownElementTypes.ATX_6 -> HeadingImpl(level = type.name.last().digitToInt())
        MarkdownElementTypes.BLOCK_QUOTE -> BlockQuoteImpl()
        MarkdownTokenTypes.HORIZONTAL_RULE -> ThematicBreakImpl()
        MarkdownElementTypes.PARAGRAPH -> ParagraphImpl()
        MarkdownTokenTypes.SETEXT_CONTENT,
        MarkdownTokenTypes.ATX_CONTENT -> {
            skipChildren = true
            TextImpl(literal = getTextInNode(input).toString().trim())
        }
        MarkdownTokenTypes.WHITE_SPACE,
        MarkdownTokenTypes.TEXT -> {
            skipChildren = true
            TextImpl(literal = getTextInNode(input).toString())
        }
        MarkdownElementTypes.CODE_FENCE -> {
            skipChildren = true
            val codeFenceContent = children.find { it.type == MarkdownTokenTypes.CODE_FENCE_CONTENT }
            val codeFenceEnd = children.find { it.type == MarkdownTokenTypes.CODE_FENCE_END }
            if (codeFenceContent != null && codeFenceEnd != null) {
                FencedCodeBlockImpl(literal = input.subSequence(codeFenceContent.startOffset, codeFenceEnd.startOffset).toString())
            } else {
                FencedCodeBlockImpl(
                    literal = children.find { it.type == MarkdownElementTypes.CODE_FENCE }
                        ?.getTextInNode(input)?.toString() ?: ""
                )
            }
        }
        MarkdownElementTypes.IMAGE -> {
            val destination = getTextInNode(input).toString()
                .substringAfter("(")
                .substringBefore(")")
            ImageImpl(destination = destination)
        }
        MarkdownElementTypes.UNORDERED_LIST -> {
            val bulletMarker = getTextInNode(input).findAnyOf(listOf("*", "-"))?.second ?: "*"
            BulletListImpl(bulletMarker = bulletMarker.single())
        }
        MarkdownElementTypes.ORDERED_LIST -> {
            val bulletMarker = getTextInNode(input).findAnyOf(listOf(".", ")"))?.second ?: "."
            OrderedListImpl(startNumber = 0, delimiter = bulletMarker.single())
        }
        MarkdownElementTypes.LIST_ITEM -> {
            ListItemImpl()
        }
        MarkdownElementTypes.STRONG -> StrongEmphasisImpl()
        MarkdownElementTypes.CODE_SPAN -> {
            skipChildren = true
            CodeImpl(literal = getTextInNode(input).toString())
        }
        MarkdownTokenTypes.HARD_LINE_BREAK -> {
            HardLineBreakImpl()
        }
        MarkdownElementTypes.INLINE_LINK -> {
            when (parent) {
                is Image -> {
                    skipChildren = true
                    TextImpl(
                        literal = children.find { it.type == MarkdownElementTypes.LINK_DESTINATION }
                            ?.getTextInNode(input)?.toString() ?: ""
                    )
                }
                else -> LinkImpl(
                    destination = children.find { it.type == MarkdownElementTypes.LINK_DESTINATION }
                        ?.getTextInNode(input)?.toString() ?: ""
                )
            }
        }
        MarkdownElementTypes.LINK_TEXT -> {
            skipChildren = true
            TextImpl(
                literal = children.find { it.type == MarkdownTokenTypes.TEXT }
                    ?.getTextInNode(input)?.toString() ?: ""
            )
        }
        else -> null
    }?.updateNode(input = input, node = this, parent = parent, skipChildren = skipChildren)
}

private fun NodeImpl.updateNode(input: String, node: ASTNode, parent: NodeType?, skipChildren: Boolean = false): NodeType {
    _parent = parent
    if (!skipChildren) {
        _children.addAll(
            node.children.mapNotNull {
                it.convert(input, this)
            }
        )
    }
    return this
}
