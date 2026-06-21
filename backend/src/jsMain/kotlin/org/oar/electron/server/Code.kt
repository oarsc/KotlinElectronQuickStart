package org.oar.electron.server

import org.oar.electron.context.Channels.CLOSE
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
        BrowserWindow.fromWebContents(event.sender)?.close()
    }
}
