package se.sekvy.compose.markdown

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG_URL = "url"
private const val TAG_IMAGE_URL = "imageUrl"

private val LocalLoadImage = compositionLocalOf<LoadImageConfig> { throw IllegalStateException("Not provided") }

private data class LoadImageConfig(
    val onLoadImage: suspend (path: String) -> ImageBitmap
)

/**
 * Entry point for composing a markdown node tree
 *
 * @param document The first node
 * @param onLoadImage A suspend function for loading images
 */
@Composable
fun ColumnScope.MDDocument(document: NodeType, onLoadImage: suspend (path: String) -> ImageBitmap) {
    CompositionLocalProvider(
        LocalLoadImage provides LoadImageConfig(onLoadImage)
    ) {
        MDBlockChildren(document)
    }
}

@Composable
private fun MDBlockChildren(parent: NodeType) {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is BlockQuote -> MDBlockQuote(child)
            is ThematicBreak -> MDThematicBreak(child)
            is Heading -> MDHeading(child)
            is Paragraph -> MDParagraph(child)
            is FencedCodeBlock -> MDFencedCodeBlock(child)
            is Image -> MDImage(child)
            is BulletList -> MDBulletList(child)
            is OrderedList -> MDOrderedList(child)
        }
        child = child.next
    }
}

private fun AnnotatedString.Builder.appendMarkdownChildren(
    parent: NodeType,
    colors: Colors
) {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is Paragraph -> appendMarkdownChildren(child, colors)
            is Text -> append(child.literal)
            is Image -> appendInlineContent(TAG_IMAGE_URL, child.destination)
            is Emphasis -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                appendMarkdownChildren(child, colors)
                pop()
            }
            is StrongEmphasis -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                appendMarkdownChildren(child, colors)
                pop()
            }
            is Code -> {
                pushStyle(TextStyle(fontFamily = FontFamily.Monospace).toSpanStyle())
                append(child.literal)
                pop()
            }
            is HardLineBreak -> {
                append("\n")
            }
            is SoftLineBreak -> {
                append(" ")
            }
            is Link -> {
                val underline = SpanStyle(colors.primary, textDecoration = TextDecoration.Underline)
                pushStyle(underline)
                pushStringAnnotation(TAG_URL, child.destination)
                appendMarkdownChildren(child, colors)
                pop()
                pop()
            }
        }
        child = child.next
    }
}

@Composable
private fun MDHeading(heading: Heading, modifier: Modifier = Modifier) {
    val style = when (heading.level) {
        1 -> MaterialTheme.typography.h1
        2 -> MaterialTheme.typography.h2
        3 -> MaterialTheme.typography.h3
        4 -> MaterialTheme.typography.h4
        5 -> MaterialTheme.typography.h5
        6 -> MaterialTheme.typography.h6
        else -> MaterialTheme.typography.h6
    }

    val padding = if (heading.parent is Document) 8.dp else 0.dp
    Box(modifier = modifier.padding(bottom = padding)) {
        val text = buildAnnotatedString {
            appendMarkdownChildren(heading, MaterialTheme.colors)
        }
        MarkdownText(text, style)
    }
}

@Composable
private fun MDParagraph(paragraph: Paragraph, modifier: Modifier = Modifier) {
    if (paragraph.firstChild is Image && paragraph.firstChild == paragraph.lastChild) {
        // Paragraph with single image
        MDImage(paragraph.firstChild as Image, modifier)
    } else {
        val padding = if (paragraph.parent is Document) 8.dp else 0.dp
        Box(modifier = modifier.padding(bottom = padding)) {
            val styledText = buildAnnotatedString {
                pushStyle(MaterialTheme.typography.body1.toSpanStyle())
                appendMarkdownChildren(paragraph, MaterialTheme.colors)
                pop()
            }
            MarkdownText(styledText, MaterialTheme.typography.body1)
        }
    }
}

@Composable
private fun MDImage(image: Image, modifier: Modifier = Modifier) {
    OnLoadedImage(modifier = modifier.padding(16.dp), path = image.destination)
}

@Composable
private fun MDBulletList(bulletList: BulletList, modifier: Modifier = Modifier) {
    val marker = bulletList.bulletMarker
    MDListItems(bulletList, modifier = modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.body1.toSpanStyle())
            append("$marker ")
            appendMarkdownChildren(it, MaterialTheme.colors)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.body1, modifier)
    }
}

@Composable
private fun MDOrderedList(orderedList: OrderedList, modifier: Modifier = Modifier) {
    var number = orderedList.startNumber
    val delimiter = orderedList.delimiter
    MDListItems(orderedList, modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.body1.toSpanStyle())
            append("${number++}$delimiter ")
            appendMarkdownChildren(it, MaterialTheme.colors)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.body1, modifier)
    }
}

@Composable
private fun MDListItems(
    listBlock: ListBlock,
    modifier: Modifier = Modifier,
    item: @Composable (node: NodeType) -> Unit
) {
    val bottom = if (listBlock.parent is Document) 8.dp else 0.dp
    val start = if (listBlock.parent is Document) 0.dp else 8.dp
    Column(modifier = modifier.padding(start = start, bottom = bottom)) {
        var listItem = listBlock.firstChild
        while (listItem != null) {
            var child = listItem.firstChild
            while (child != null) {
                when (child) {
                    is BulletList -> MDBulletList(child, modifier)
                    is OrderedList -> MDOrderedList(child, modifier)
                    else -> item(child)
                }
                child = child.next
            }
            listItem = listItem.next
        }
    }
}

@Composable
private fun MDBlockQuote(blockQuote: BlockQuote, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colors.onBackground

    Box(
        modifier = modifier
            .drawBehind {
                drawLine(
                    color = color,
                    strokeWidth = 2f,
                    start = Offset(12.dp.value, 12.dp.value),
                    end = Offset(12.dp.value, size.height - 12.dp.value)
                )
            }
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
    ) {
        val text = buildAnnotatedString {
            pushStyle(
                MaterialTheme.typography.body1.toSpanStyle()
                    .plus(SpanStyle(fontStyle = FontStyle.Italic))
            )
            appendMarkdownChildren(blockQuote, MaterialTheme.colors)
            pop()
        }
        Text(text, modifier)
    }
}

@Composable
private fun MDFencedCodeBlock(fencedCodeBlock: FencedCodeBlock, modifier: Modifier = Modifier) {
    val padding = if (fencedCodeBlock.parent is Document) 8.dp else 0.dp
    Box(modifier = modifier.padding(start = 8.dp, bottom = padding)) {
        Text(
            text = fencedCodeBlock.literal,
            style = TextStyle(fontFamily = FontFamily.Monospace),
            modifier = modifier
        )
    }
}

@Composable
private fun MDThematicBreak(thematicBreak: ThematicBreak, modifier: Modifier = Modifier) {
    // Ignored
}

@Composable
private fun MarkdownText(text: AnnotatedString, style: TextStyle, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = text,
        modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                layoutResult.value?.let { layoutResult ->
                    val position = layoutResult.getOffsetForPosition(offset)
                    text.getStringAnnotations(position, position)
                        .firstOrNull()
                        ?.let { sa ->
                            if (sa.tag == TAG_URL) {
                                uriHandler.openUri(sa.item)
                            }
                        }
                }
            }
        },
        style = style,
        inlineContent = mapOf(
            TAG_IMAGE_URL to InlineTextContent(
                Placeholder(style.fontSize, style.fontSize, PlaceholderVerticalAlign.Bottom)
            ) {
                OnLoadedImage(modifier = modifier, path = it)
            }
        ),
        onTextLayout = { layoutResult.value = it }
    )
}

@Composable
private fun OnLoadedImage(
    modifier: Modifier = Modifier,
    path: String
) {
    var bitmap: ImageBitmap? by remember {
        mutableStateOf(null)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        bitmap?.let {
            Image(
                contentScale = ContentScale.Fit,
                bitmap = it,
                contentDescription = ""
            )
        }
    }
    val loadBitmap = LocalLoadImage.current.onLoadImage
    LaunchedEffect(path) {
        withContext(Dispatchers.Default) {
            bitmap = loadBitmap(path)
        }
    }
}
