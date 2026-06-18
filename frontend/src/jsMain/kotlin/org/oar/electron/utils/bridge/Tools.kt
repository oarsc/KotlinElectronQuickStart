package org.oar.electron.utils.bridge

import kotlinx.browser.window

object Tools {
    private val tools = window.asDynamic().tools
    fun getMode(): String? = tools.getMode()
}
