@file:Suppress("unused", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("commonmark")
@file:JsNonModule

open external class Node(nodeType: String, sourcepos: Any /* JsTuple<Any, Any> */ = definedExternally) {
    open var type: String /* "text" | "softbreak" | "linebreak" | "emph" | "strong" | "html_inline" | "link" | "image" | "code" | "document" | "paragraph" | "block_quote" | "item" | "list" | "heading" | "code_block" | "html_block" | "thematic_break" | "custom_inline" | "custom_block" */
    open var firstChild: Node?
    open var lastChild: Node?
    open var next: Node?
    open var prev: Node?
    open var parent: Node?
    open var sourcepos: dynamic /* JsTuple<dynamic, dynamic> */
    open var isContainer: Boolean
    open var literal: String?
    open var destination: String?
    open var title: String?
    open var info: String?
    open var level: Number
    open var listType: String /* "Bullet" | "Ordered" */
    open var listTight: Boolean
    open var listStart: Number
    open var listDelimiter: String /* ")" | "." */
    open var onEnter: String
    open var onExit: String
    open fun appendChild(child: Node)
    open fun prependChild(child: Node)
    open fun unlink()
    open fun insertAfter(sibling: Node)
    open fun insertBefore(sibling: Node)
    open fun walker(): NodeWalker
    open var _listData: ListData
}

open external class Parser(options: ParserOptions = definedExternally) {
    open fun parse(input: String): Node
}

external interface NodeWalkingStep {
    var entering: Boolean
    var node: Node
}

external interface NodeWalker {
    fun next(): NodeWalkingStep
    fun resumeAt(node: Node, entering: Boolean = definedExternally)
}

external interface ListData {
    var type: String?
        get() = definedExternally
        set(value) = definedExternally
    var tight: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var delimiter: String?
        get() = definedExternally
        set(value) = definedExternally
    var bulletChar: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ParserOptions {
    var smart: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var time: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}
