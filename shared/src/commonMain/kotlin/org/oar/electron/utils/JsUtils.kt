package org.oar.electron.utils

object JsUtils {
    inline fun <T> jsObject(builder: T.() -> Unit): T {
        val obj = js("{}") as T
        obj.builder()
        return obj
    }

    inline fun <T> jsArray(builder: MutableList<T>.() -> Unit): Array<T> =
        buildList(builder).toTypedArray()
}