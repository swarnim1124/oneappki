package com.xsc.oneapp.sdk.network

import kotlinx.serialization.json.Json

object SerializationUtils {

    val defaultJson: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = false
        coerceInputValues = true
    }

    inline fun <reified T> encodeToString(value: T): String {
        return defaultJson.encodeToString(T::class.serializer(), value)
    }

    inline fun <reified T> decodeFromString(json: String): T {
        return defaultJson.decodeFromString(json)
    }
}
