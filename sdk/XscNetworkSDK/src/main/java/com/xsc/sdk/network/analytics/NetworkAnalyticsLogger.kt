package com.xsc.sdk.network.analytics

/**
 * Interface to log network-level analytics.
 * This decouples the Network SDK from the app's core AnalyticsManager.
 */
interface NetworkAnalyticsLogger {
    fun logNetworkRequest(method: String, path: String, durationMs: Long, statusCode: Int)
    fun logNetworkError(method: String, path: String, exceptionMessage: String)
}
