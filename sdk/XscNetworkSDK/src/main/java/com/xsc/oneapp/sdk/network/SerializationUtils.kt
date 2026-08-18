package com.xsc.oneapp.sdk.network

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object SerializationUtils {

    val defaultJson: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = false
        coerceInputValues = true
    }

    inline fun <reified T> encodeToString(value: T): String {
        return defaultJson.encodeToString(serializer(), value)
    }

    inline fun <reified T> decodeFromString(json: String): T {
        return defaultJson.decodeFromString(json)
    }
}
