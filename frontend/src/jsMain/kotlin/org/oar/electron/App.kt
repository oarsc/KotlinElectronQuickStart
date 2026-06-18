package org.oar.electron

import kotlinx.browser.document

class App {
    fun start() {
        val root = document.getElementById("root")

        root?.innerHTML = """
            <h1>Hello from Kotlin/JS 👋</h1>
            <p>Electron + Kotlin modern setup</p>
        """.trimIndent()
    }
}