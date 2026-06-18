package org.oar.electron.server.model

@JsModule("path")
@JsNonModule
external object Path {
    fun join(vararg parts: String): String
    fun resolve(vararg parts: String): String
    fun dirname(path: String): String
}

external val __dirname: String


@JsName("process")
internal external object Process {
    val platform: String
    val arch: String
    val version: String
    val env: dynamic
    val argv: Array<String>
}
