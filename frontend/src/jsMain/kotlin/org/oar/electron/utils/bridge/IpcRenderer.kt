package org.oar.electron.utils.bridge

import kotlinx.browser.window
import org.oar.electron.utils.JsonExtensions.parse
import org.oar.electron.utils.JsonExtensions.stringify

object IpcRenderer {
    private val ipcRenderer = window.asDynamic().ipcRenderer

    fun send(channel: String, vararg params: Any?) = ipcRenderer.send(channel, params)
    fun sendSync(channel: String, vararg params: Any?): Any? = ipcRenderer.sendSync(channel, params)
    fun receive(channel: String, callback: (Array<Any?>) -> Unit) {
        ipcRenderer.receive(channel) {
            callback(js("Array.from(arguments)"))
        }
    }

    inline fun <reified T> sendJson(channel: String, param: T) = send(channel, param?.stringify())
    inline fun <reified T> sendJsonSync(channel: String, param: T): Any? = sendSync(channel, param?.stringify())
    inline fun <reified T, reified O> sendAndReceiveJsonSync(channel: String, param: T): O {
        val result = sendSync(channel, param?.stringify()) as? String
        return if (result == null) null as O else result.parse<O>()
    }
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> receiveJson(channel: String, crossinline callback: (T) -> Unit) {
        receive(channel) {
            it as Array<String?>
            callback(it.getOrNull(0)?.parse<T>() as T)
        }
    }
}