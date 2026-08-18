package com.xsc.sdk.media

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A centralized image loading abstraction wrapping Coil.
 * This ensures standardized caching and configuration across the app.
 */
@Singleton
class MediaImageLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false) // Default to app-level caching rules
            .build()
    }
}
