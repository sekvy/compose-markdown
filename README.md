# compose-markdown
Kotlin Multiplatform library for rendering Markdown with Compose

```kotlin
commonMain {
    dependencies {
        // ...
        implementation("se.sekvy:compose-markdown:1.0.0-alpha01")
    }
}
```

# Recognition

Compose code is orignally based on
https://github.com/ErikHellman/MarkdownComposer

Markdown parsing uses Commonmark libraries.
[commonmark-java](https://github.com/commonmark/commonmark-java) for jvm,
[commonmark.js](https://github.com/commonmark/commonmark.js) for Js
