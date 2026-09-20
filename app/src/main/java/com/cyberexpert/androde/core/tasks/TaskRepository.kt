package com.cyberexpert.androde.core.tasks

import kotlinx.coroutines.flow.Flow

/**
 * Tasks and Launch repository - Phase 8, similar to VS Code tasks.json and launch.json.
 * Provides real parsing and execution for tasks and debug launch configs.
 */

data class TaskDefinition(
    val label: String,
    val type: String, // shell, npm, gradle, etc.
    val command: String,
    val args: List<String> = emptyList(),
    val group: TaskGroup? = null,
    val presentation: TaskPresentation = TaskPresentation(),
    val problemMatcher: List<String> = emptyList(),
    val options: TaskOptions = TaskOptions(),
    val isBackground: Boolean = false,
    val dependsOn: List<String> = emptyList()
)

data class TaskGroup(
    val kind: String, // build, test
    val isDefault: Boolean = false
)

data class TaskPresentation(
    val echo: Boolean = true,
    val reveal: String = "always", // always, silent, never
    val focus: Boolean = false,
    val panel: String = "shared", // shared, dedicated, new
    val showReuseMessage: Boolean = true,
    val clear: Boolean = false
)

data class TaskOptions(
    val cwd: String? = null,
    val env: Map<String, String> = emptyMap()
)

data class LaunchConfiguration(
    val name: String,
    val type: String, // kotlin, java, python, node, etc.
    val request: String, // launch, attach
    val program: String? = null,
    val args: List<String> = emptyList(),
    val env: Map<String, String> = emptyMap(),
    val cwd: String? = null,
    val console: String = "integratedTerminal",
    val stopOnEntry: Boolean = false,
    val preLaunchTask: String? = null
)

data class TasksFile(
    val version: String = "2.0.0",
    val tasks: List<TaskDefinition> = emptyList()
)

data class LaunchFile(
    val version: String = "0.2.0",
    val configurations: List<LaunchConfiguration> = emptyList()
)

interface TaskRepository {
    fun getTasks(workspacePath: String): Flow<List<TaskDefinition>>
    fun getLaunchConfigurations(workspacePath: String): Flow<List<LaunchConfiguration>>
    suspend fun loadTasksFile(workspacePath: String): TasksFile?
    suspend fun loadLaunchFile(workspacePath: String): LaunchFile?
    suspend fun saveTasksFile(workspacePath: String, tasksFile: TasksFile): Boolean
    suspend fun saveLaunchFile(workspacePath: String, launchFile: LaunchFile): Boolean
    suspend fun executeTask(task: TaskDefinition, workspacePath: String): TaskExecutionResult
}

data class TaskExecutionResult(
    val success: Boolean,
    val exitCode: Int,
    val output: String,
    val error: String? = null,
    val taskLabel: String
)
