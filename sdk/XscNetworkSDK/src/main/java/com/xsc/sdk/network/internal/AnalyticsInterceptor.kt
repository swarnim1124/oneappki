package com.xsc.sdk.network.internal

import com.xsc.sdk.network.analytics.NetworkAnalyticsLogger
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that delegates network event logging to [NetworkAnalyticsLogger].
 * Logs request paths, latency, and status codes.
 */
@Singleton
class AnalyticsInterceptor @Inject constructor(
    private val logger: NetworkAnalyticsLogger
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method
        val startNs = System.nanoTime()

        try {
            val response = chain.proceed(request)
            val durationMs = (System.nanoTime() - startNs) / 1_000_000L
            logger.logNetworkRequest(method, path, durationMs, response.code)
            return response
        } catch (e: IOException) {
            logger.logNetworkError(method, path, e.message ?: "Unknown IOException")
            throw e
        }
    }
}
