package com.xsc.oneapp.core.security

import android.os.Build
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmulatorDetector @Inject constructor() {

    fun isEmulator(): Boolean {
        return checkBuildHardware() ||
                checkFingerprint() ||
                checkPipes()
    }

    private fun checkBuildHardware(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.BOARD.lowercase().contains("nox")
                || Build.BOOTLOADER.lowercase().contains("nox")
                || Build.HARDWARE.lowercase().contains("nox")
                || Build.PRODUCT.lowercase().contains("nox"))
    }

    private fun checkFingerprint(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.lowercase().contains("vbox")
                || Build.FINGERPRINT.lowercase().contains("test-keys"))
    }

    private fun checkPipes(): Boolean {
        val pipes = arrayOf(
            "/dev/socket/qemud",
            "/dev/qemu_pipe",
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
        )
        return pipes.any { File(it).exists() }
    }
}
