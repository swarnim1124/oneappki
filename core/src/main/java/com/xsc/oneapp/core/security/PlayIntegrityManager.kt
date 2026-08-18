package com.xsc.oneapp.core.security

import android.content.Context
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayIntegrityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val integrityManager by lazy {
        IntegrityManagerFactory.create(context)
    }

    suspend fun requestIntegrityToken(
        cloudProjectNumber: Long,
        nonce: String
    ): Result<String> {
        return try {
            val request = IntegrityTokenRequest.builder()
                .setCloudProjectNumber(cloudProjectNumber)
                .setNonce(nonce)
                .build()

            val response = integrityManager.requestIntegrityToken(request).await()
            Result.success(response.token())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
