package com.xsc.sdk.file

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A centralized utility for handling file operations, Uri resolutions, and Scoped Storage
 * caching. Prevents feature modules from directly dealing with Android filesystem quirks.
 */
@Singleton
class FileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Resolves the MIME type of a given Uri.
     */
    fun getMimeType(uri: Uri): String? {
        return if (uri.scheme == "content") {
            context.contentResolver.getType(uri)
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        }
    }

    /**
     * Securely copies a content Uri into the app's internal cache directory,
     * so it can be uploaded or processed as a standard java.io.File.
     */
    suspend fun cacheUriToFile(uri: Uri, prefix: String = "cache_"): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val tempFile = File.createTempFile(prefix, null, context.cacheDir)
                val outputStream = FileOutputStream(tempFile)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                return@withContext tempFile
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}
