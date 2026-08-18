package com.xsc.oneapp.core.network

import android.os.Bundle
import com.xsc.oneapp.core.firebase.AnalyticsManager
import com.xsc.sdk.network.analytics.NetworkAnalyticsLogger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [NetworkAnalyticsLogger] that routes network events to Firebase Analytics.
 */
@Singleton
class CoreNetworkAnalyticsLogger @Inject constructor(
    private val analyticsManager: AnalyticsManager
) : NetworkAnalyticsLogger {

    override fun logNetworkRequest(method: String, path: String, durationMs: Long, statusCode: Int) {
        val params = Bundle().apply {
            putString("api_path", path)
            putString("api_method", method)
            putLong("duration_ms", durationMs)
            putInt("status_code", statusCode)
        }
        analyticsManager.logEvent("network_request", params)
    }

    override fun logNetworkError(method: String, path: String, exceptionMessage: String) {
        val params = Bundle().apply {
            putString("api_path", path)
            putString("api_method", method)
            putString("error_message", exceptionMessage)
        }
        analyticsManager.logEvent("network_error", params)
    }
}
