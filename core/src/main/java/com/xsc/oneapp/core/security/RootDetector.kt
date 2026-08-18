package com.xsc.oneapp.core.security

import android.content.Context
import android.os.Build
import com.scottyab.rootbeer.RootBeer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isRooted(): Boolean {
        return checkBuildTags() ||
                checkSuPaths() ||
                checkSuperUserApks() ||
                checkRootBeer()
    }

    private fun checkBuildTags(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkSuPaths(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun checkSuperUserApks(): Boolean {
        val knownPackages = arrayOf(
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.zacharee1.systemuituner",
            "com.topjohnwu.magisk"
        )
        val pm = context.packageManager
        return knownPackages.any { pkg ->
            try {
                pm.getPackageInfo(pkg, 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun checkRootBeer(): Boolean {
        return try {
            val rootBeer = RootBeer(context)
            rootBeer.isRooted
        } catch (e: Exception) {
            false
        }
    }
}
