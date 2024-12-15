# compose-markdown
Kotlin Multiplatform library for rendering Markdown with Compose

```kotlin
commonMain {
    dependencies {
        // ...
        implementation("se.sekvy:compose-markdown:1.0.2-alpha")
    }
}
```

# Usage
For example

```kotlin
@Composable
fun showArticle(path: String) {
    var nodeType: NodeType? by remember { mutableStateOf(null) }

    LaunchedEffect(path) {
        val bytes = loadBytesFromPath(path)
        nodeType = MarkdownParser(ParserType.CommonMark).parse(bytes.decodeToString())
    }

    nodeType?.let {
        Box(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)) {
            Column {
                MDDocument(it) {
                    loadBitmap(it)
                }
            }
        }
    }
}
```

# Recognition

Compose code is orignally based on
https://github.com/ErikHellman/MarkdownComposer

Markdown parsing uses Commonmark libraries.
[commonmark-java](https://github.com/commonmark/commonmark-java) for jvm,
[commonmark.js](https://github.com/commonmark/commonmark.js) for Js. Or [JetBrains/markdown](https://github.com/JetBrains/markdown) for wasm, js and jvm

