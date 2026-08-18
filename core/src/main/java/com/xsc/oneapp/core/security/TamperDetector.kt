package com.xsc.oneapp.core.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TamperDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getAppSignatureHash(): String? {
        return try {
            val pm = context.packageManager
            val packageName = context.packageName

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (!signatures.isNullOrEmpty()) {
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(signatures[0].toByteArray())
                digest.joinToString("") { "%02X".format(it) }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun isAppTampered(expectedSignatureHash: String?): Boolean {
        if (expectedSignatureHash.isNullOrBlank()) return false
        val currentHash = getAppSignatureHash() ?: return false
        return !currentHash.equals(expectedSignatureHash, ignoreCase = true)
    }
}
