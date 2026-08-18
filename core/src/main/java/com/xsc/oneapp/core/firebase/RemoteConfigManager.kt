package com.xsc.oneapp.core.firebase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

/**
 * A central wrapper around FirebaseRemoteConfig to manage fetching and activating remote flags safely.
 */
@Singleton
class RemoteConfigManager @Inject constructor() {

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600) // 1 hour for prod, drop for debug if needed
                .build()
            setConfigSettingsAsync(configSettings)
        }
    }

    /**
     * Fetch and activate remote configurations.
     */
    suspend fun fetchAndActivate(): Boolean {
        return try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            // Log to Crashlytics or Analytics
            false
        }
    }

    /**
     * Get a string value from Remote Config.
     */
    fun getString(key: String): String {
        return remoteConfig.getString(key)
    }

    /**
     * Get a boolean value from Remote Config.
     */
    fun getBoolean(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }
}
