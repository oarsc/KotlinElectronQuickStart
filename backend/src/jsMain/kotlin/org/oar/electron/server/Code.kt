package org.oar.electron.server

import org.oar.electron.context.Channels.CLOSE
import org.oar.electron.context.Channels.SET_IGNORE_MOUSE_EVENTS
import org.oar.electron.jsModels.IgnoreMouseEventsOptions
import org.oar.electron.server.model.BrowserWindow
import org.oar.electron.server.model.BrowserWindowOptions
import org.oar.electron.server.model.Electron.BrowserWindow
import org.oar.electron.server.model.Electron.app
import org.oar.electron.server.model.Electron.ipcMain
import org.oar.electron.server.model.Path
import org.oar.electron.server.model.Process
import org.oar.electron.server.model.WebPreferences
import org.oar.electron.server.model.__dirname

var mainWindow: BrowserWindow? = null

fun createWindow(): BrowserWindow = BrowserWindow(
    BrowserWindowOptions {
        width = 1200
        height = 800
        show = true

        webPreferences = WebPreferences {
            preload = Path.join(__dirname, "./bridge.js")
            contextIsolation = true
        }
    }
).apply {
    loadFile(Path.join(__dirname, "../web/index.html"))
    on("closed") {
        mainWindow = null
    }
}

fun main() {
    app.whenReady().then {
        mainWindow = createWindow()
    }

    app.on("window-all-closed") {
        if (Process.platform !== "darwin") app.quit()
    }

    app.on("activate") {
        if (mainWindow == null) {
            createWindow()
        }
    }

    ipcMain.on(CLOSE) { event, _ ->
        BrowserWindow.fromWebContents(event.sender)?.destroy()
    }

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    ipcMain.on(SET_IGNORE_MOUSE_EVENTS) { event, args ->
        val ignore = args[0] as Boolean
        val options = args.getOrNull(1) as? IgnoreMouseEventsOptions
        BrowserWindow.fromWebContents(event.sender)?.setIgnoreMouseEvents(ignore, options)
    }
}
