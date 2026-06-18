package org.oar.electron.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

object JsonExtensions {
    val JSON = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    fun String.toJsonElement(): JsonElement = JSON.parseToJsonElement(this)
    inline fun <reified T> JsonElement.parse(): T = JSON.decodeFromJsonElement<T>(this)
    inline fun <reified T> String.parse(): T = JSON.decodeFromString<T>(this)
    inline fun <reified T> T.jsonify() = JSON.encodeToJsonElement(this)
    inline fun <reified T> T.stringify() = JSON.encodeToString(this)
}