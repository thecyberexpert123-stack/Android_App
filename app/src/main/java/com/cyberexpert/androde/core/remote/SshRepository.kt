package com.cyberexpert.androde.core.remote

import kotlinx.coroutines.flow.Flow

/**
 * SSH repository for remote development, similar to VS Code Remote SSH.
 * Provides SSH connection, file browsing, command execution on remote host.
 * Uses JSch for SSH (pure Java, works on Android).
 * Production-ready with security, key handling, error handling.
 */
interface SshRepository {
    fun getConnections(): Flow<List<SshConnection>>
    suspend fun connect(config: SshConfig): Result<SshConnection>
    suspend fun disconnect(connectionId: String): Boolean
    suspend fun executeCommand(connectionId: String, command: String): Result<SshCommandResult>
    suspend fun listFiles(connectionId: String, remotePath: String): Result<List<SshFile>>
    suspend fun downloadFile(connectionId: String, remotePath: String, localPath: String): Result<Boolean>
    suspend fun uploadFile(connectionId: String, localPath: String, remotePath: String): Result<Boolean>
}

data class SshConfig(
    val host: String,
    val port: Int = 22,
    val username: String,
    val password: String? = null,
    val privateKeyPath: String? = null,
    val passphrase: String? = null,
    val knownHostsPath: String? = null
)

data class SshConnection(
    val id: String,
    val config: SshConfig,
    val isConnected: Boolean,
    val connectedAt: Long
)

data class SshCommandResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val command: String
)

data class SshFile(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val permissions: String,
    val lastModified: Long
)
