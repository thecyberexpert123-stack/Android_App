package com.cyberexpert.androde.core.filesystem

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SAF Repository for Android 11+ scoped storage.
 * Similar to VS Code's file system provider for virtual and remote file systems.
 * Handles Storage Access Framework URIs and DocumentFile.
 */
@Singleton
class SafRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun createOpenFolderIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
    }

    suspend fun persistPermission(uri: Uri) = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getDisplayName(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val docFile = DocumentFile.fromTreeUri(context, uri)
            docFile?.name ?: uri.lastPathSegment ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    suspend fun listFilesFromSaf(uri: Uri): List<File> = withContext(Dispatchers.IO) {
        try {
            val docFile = DocumentFile.fromTreeUri(context, uri) ?: return@withContext emptyList()
            docFile.listFiles().mapNotNull { doc ->
                // For MVP, we try to get File from DocumentFile if possible
                // In production, work with DocumentFile directly
                try {
                    File(doc.uri.path ?: return@mapNotNull null)
                } catch (e: Exception) { null }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun isSafUri(path: String): Boolean = path.startsWith("content://")

    companion object {
        const val PREF_SAF_URI = "saf_uri"
    }
}
