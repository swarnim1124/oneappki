package com.xsc.sdk.camera

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * A centralized wrapper for CameraX operations to ensure consistent behavior,
 * lifecycle handling, and standard image capture capabilities across features.
 */
@Singleton
class CameraManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Initializes the CameraProvider asynchronously.
     */
    suspend fun getCameraProvider(): ProcessCameraProvider = suspendCancellableCoroutine { continuation ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                continuation.resume(cameraProviderFuture.get())
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Creates a standardized ImageCapture use case.
     */
    fun buildImageCaptureUseCase(): ImageCapture {
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
}
