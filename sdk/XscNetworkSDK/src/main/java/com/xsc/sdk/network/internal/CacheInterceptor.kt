package com.xsc.sdk.network.internal

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that rewrites cache headers to force caching of GET requests.
 * OneApp primarily relies on POST for its dispatcher, but this sets up standard
 * offline-first fallback capabilities for future GET usage.
 */
@Singleton
class CacheInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Don't cache POST requests or anything other than GET
        if (request.method != "GET") {
            return chain.proceed(request)
        }

        val response = chain.proceed(request)
        
        // Cache GET responses for 5 minutes (300 seconds)
        return response.newBuilder()
            .header("Cache-Control", "public, max-age=" + 300)
            .removeHeader("Pragma")
            .build()
    }
}
