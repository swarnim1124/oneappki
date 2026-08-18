package com.xsc.oneapp.core.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A central wrapper around FirebaseAnalytics to prevent tight coupling across feature modules.
 */
@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(context)
    }

    /**
     * Log a custom event to Firebase Analytics.
     */
    fun logEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    /**
     * Set a custom user property.
     */
    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    /**
     * Set the current user ID.
     */
    fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }
}
