package com.xsc.sdk.network.internal

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that retries failed network requests.
 * Uses a simple exponential backoff for 5xx errors or network timeouts.
 */
@Singleton
class RetryInterceptor @Inject constructor() : Interceptor {
    private val maxRetries = 3
    private val initialDelayMs = 1000L

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var attempt = 0
        var delayMs = initialDelayMs
        var exception: IOException? = null

        while (attempt < maxRetries) {
            try {
                response = chain.proceed(request)
                // If successful or client error (4xx), return immediately.
                if (response.isSuccessful || (response.code in 400..499)) {
                    return response
                }
                
                // For 5xx errors, we retry. Close previous response body before retrying.
                response.close()
            } catch (e: IOException) {
                exception = e
            }

            attempt++
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(delayMs)
                } catch (ie: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
                delayMs *= 2 // Exponential backoff
            }
        }

        if (response != null) {
            return response
        }
        
        throw exception ?: IOException("Unknown network error during retry.")
    }
}
