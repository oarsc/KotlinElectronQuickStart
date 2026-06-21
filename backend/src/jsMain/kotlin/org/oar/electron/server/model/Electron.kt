package org.oar.electron.server.model

import org.oar.electron.jsModels.IgnoreMouseEventsOptions
import org.oar.electron.utils.JsUtils.jsObject
import org.oar.electron.utils.JsonExtensions.parse
import org.oar.electron.utils.JsonExtensions.stringify
import kotlin.js.Promise

object Electron {
    val native: dynamic = ElectronNative
    val app get() = ElectronNative.app
    val ipcMain get() = ElectronNative.ipcMain
    val screen get() = ElectronNative.screen
    val globalShortcut get() = ElectronNative.globalShortcut
    val BrowserWindow = StaticBrowserWindow(ElectronNative.BrowserWindow)
}

class StaticBrowserWindow(val native: dynamic) {
    operator fun invoke(options: BrowserWindowOptions): BrowserWindow {
        val windowConstructor = native
        return js("new windowConstructor(options)")
    }

    fun fromWebContents(webContents: WebContents): BrowserWindow? =
        native.fromWebContents(webContents)
}

fun WebPreferences(builder: WebPreferences.() -> Unit) = jsObject(builder)
fun BrowserWindowOptions(builder: BrowserWindowOptions.() -> Unit) = jsObject(builder)

// MODEL

@JsModule("electron")
@JsNonModule
private external object ElectronNative {
    val app: ElectronApp
    val ipcMain: IpcMain
    val screen: Screen
    val globalShortcut: GlobalShortcut
    val BrowserWindow: dynamic
}

external object ElectronApp {
    fun whenReady(): Promise<Unit>
    fun on(event: String, listener: (Array<Any?>) -> Unit): ElectronApp
    fun quit()
}

external object IpcMain {
    fun on(channel: String, listener: (IpcMainEvent, Array<Any?>) -> Unit): IpcMain
}
@Suppress("UNCHECKED_CAST")
inline fun <reified T> IpcMain.onJson(channel: String, crossinline callback: (IpcMainEvent, T) -> Unit) {
    on(channel) { event, params ->
        params as Array<String?>
        callback(event, params.getOrNull(0)?.parse<T>() as T)
    }
}

external object IpcMainEvent {
    val sender: WebContents
    var returnValue: Any?
}

external object GlobalShortcut {
    fun isRegistered(accelerator: String): Boolean
    fun register(accelerator: String, callback: () -> Unit): Boolean
    fun registerAll(accelerators: Array<String>, callback: () -> Unit)
    fun unregister(accelerator: String)
    fun unregisterAll()
}

external class BrowserWindow private constructor(options: BrowserWindowOptions) {
    val webContents: WebContents

    fun destroy()
    fun close()
    fun show()
    fun hide()
    fun minimize()
    fun isVisible(): Boolean
    fun loadURL(url: String)
    fun loadFile(url: String)
    fun on(event: String, listener: (dynamic) -> Unit): BrowserWindow
    fun setIgnoreMouseEvents(ignore: Boolean, options: IgnoreMouseEventsOptions?)
    fun getBounds(): Rectangle
    fun getPosition(): Array<Int>
    fun setPosition(x: Int, y: Int)
    fun setPosition(x: Int, y: Int, animate: Boolean)
    fun setBounds(bounds: Rectangle, animate: Boolean)
    fun setBounds(bounds: Rectangle)
}

external interface WebContents {
    fun send(channel: String, vararg args: Any?)
}
inline fun <reified T> WebContents.sendJson(channel: String, param: T) = send(channel, param?.stringify())

external interface BrowserWindowOptions {
    var width: Int?
    var height: Int?
    var x: Int?
    var y: Int?

    var show: Boolean?
    var frame: Boolean?
    var resizable: Boolean?
    var center: Boolean?
    var fullscreen: Boolean?
    var alwaysOnTop: Boolean?
    var skipTaskbar: Boolean?
    var autoHideMenuBar: Boolean?
    var movable: Boolean?
    var transparent: Boolean?

    var title: String?

    var webPreferences: WebPreferences?
}

external interface WebPreferences {
    var preload: String?
    var contextIsolation: Boolean?
    var nodeIntegration: Boolean?
    var sandbox: Boolean?
    var additionalArguments: Array<String>?
}

external object Screen {
    fun getDisplayNearestPoint(point: Point): Display
    fun getPrimaryDisplay(): Display
}

external object Display {
    var bounds: Rectangle
}

external object Point {
    var x: Int
    var y: Int
}

external object Rectangle {
    var x: Int
    var y: Int
    var height: Int
    var width: Int
}