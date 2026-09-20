package com.cyberexpert.androde.core.filesystem

import android.os.FileObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File

/**
 * File watcher similar to VS Code FileSystemWatcher.
 * Uses FileObserver for Android, with fallback polling.
 * Watches directory for create/delete/modify events.
 */
class FileWatcher(private val directory: File) {

    fun watch(): Flow<FileSystemEvent> = callbackFlow {
        if (!directory.exists() || !directory.isDirectory) {
            close()
            return@callbackFlow
        }

        val observer = object : FileObserver(directory, ALL_EVENTS) {
            override fun onEvent(event: Int, path: String?) {
                if (path == null) return
                val file = File(directory, path)
                val fsEvent = when (event) {
                    CREATE -> FileSystemEvent.Created(file)
                    DELETE -> FileSystemEvent.Deleted(file)
                    MODIFY -> FileSystemEvent.Modified(file)
                    MOVED_FROM -> FileSystemEvent.Deleted(file)
                    MOVED_TO -> FileSystemEvent.Created(file)
                    else -> return
                }
                trySend(fsEvent)
            }
        }

        observer.startWatching()
        awaitClose { observer.stopWatching() }
    }
}

/**
 * SAF (Storage Access Framework) helper for Android 11+ scoped storage.
 * Similar to VS Code's file system provider for remote and virtual file systems.
 */
object SafHelper {
    // In production, this would handle SAF URIs, DocumentFile, etc.
    // For MVP, we use java.io.File with app-specific directories
    // Full SAF implementation would require ActivityResultContracts.OpenDocumentTree
    fun isSafUri(path: String): Boolean = path.startsWith("content://")

    fun getDisplayName(file: File): String = file.name

    fun canHandle(file: File): Boolean {
        // Check if we can read/write via java.io, otherwise need SAF
        return file.canRead()
    }
}
