package com.cyberexpert.androde.data.local.tasks

import android.util.Log
import com.cyberexpert.androde.core.tasks.LaunchConfiguration
import com.cyberexpert.androde.core.tasks.LaunchFile
import com.cyberexpert.androde.core.tasks.TaskDefinition
import com.cyberexpert.androde.core.tasks.TaskExecutionResult
import com.cyberexpert.androde.core.tasks.TaskRepository
import com.cyberexpert.androde.core.tasks.TasksFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real working Tasks and Launch repository - Phase 8 with real JSON parsing and execution.
 * Similar to VS Code tasks.json and launch.json, provides load, save, execute.
 * Production-ready with Dispatchers.IO, JSON parsing, ProcessBuilder execution.
 */
@Singleton
class TaskRepositoryImpl @Inject constructor() : TaskRepository {

    private val _tasks = MutableStateFlow<List<TaskDefinition>>(emptyList())
    private val _launchConfigs = MutableStateFlow<List<LaunchConfiguration>>(emptyList())

    override fun getTasks(workspacePath: String): Flow<List<TaskDefinition>> = _tasks.asStateFlow()
    override fun getLaunchConfigurations(workspacePath: String): Flow<List<LaunchConfiguration>> = _launchConfigs.asStateFlow()

    override suspend fun loadTasksFile(workspacePath: String): TasksFile? = withContext(Dispatchers.IO) {
        try {
            val tasksFile = File(workspacePath, ".vscode/tasks.json")
            if (!tasksFile.exists()) {
                Log.d("TaskRepo", "tasks.json not found at ${tasksFile.absolutePath}")
                return@withContext null
            }

            val content = tasksFile.readText()
            val json = JSONObject(content)
            val version = json.optString("version", "2.0.0")
            val tasksArray = json.optJSONArray("tasks") ?: JSONArray()

            val tasks = mutableListOf<TaskDefinition>()
            for (i in 0 until tasksArray.length()) {
                try {
                    val taskObj = tasksArray.getJSONObject(i)
                    val label = taskObj.optString("label", "Task $i")
                    val type = taskObj.optString("type", "shell")
                    val command = taskObj.optString("command", "")
                    val argsArray = taskObj.optJSONArray("args")
                    val args = mutableListOf<String>()
                    if (argsArray != null) {
                        for (j in 0 until argsArray.length()) {
                            args.add(argsArray.optString(j, ""))
                        }
                    }

                    tasks.add(
                        TaskDefinition(
                            label = label,
                            type = type,
                            command = command,
                            args = args,
                            isBackground = taskObj.optBoolean("isBackground", false)
                        )
                    )
                } catch (e: Exception) {
                    Log.w("TaskRepo", "Failed to parse task $i", e)
                }
            }

            val tasksFileObj = TasksFile(version = version, tasks = tasks)
            _tasks.value = tasks
            Log.i("TaskRepo", "Loaded ${tasks.size} tasks from ${tasksFile.absolutePath}")
            tasksFileObj
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to load tasks.json from $workspacePath", e)
            null
        }
    }

    override suspend fun loadLaunchFile(workspacePath: String): LaunchFile? = withContext(Dispatchers.IO) {
        try {
            val launchFile = File(workspacePath, ".vscode/launch.json")
            if (!launchFile.exists()) {
                Log.d("TaskRepo", "launch.json not found at ${launchFile.absolutePath}")
                return@withContext null
            }

            val content = launchFile.readText()
            val json = JSONObject(content)
            val version = json.optString("version", "0.2.0")
            val configsArray = json.optJSONArray("configurations") ?: JSONArray()

            val configs = mutableListOf<LaunchConfiguration>()
            for (i in 0 until configsArray.length()) {
                try {
                    val configObj = configsArray.getJSONObject(i)
                    val name = configObj.optString("name", "Launch $i")
                    val type = configObj.optString("type", "kotlin")
                    val request = configObj.optString("request", "launch")
                    val program = configObj.optString("program", null)

                    configs.add(
                        LaunchConfiguration(
                            name = name,
                            type = type,
                            request = request,
                            program = program
                        )
                    )
                } catch (e: Exception) {
                    Log.w("TaskRepo", "Failed to parse launch config $i", e)
                }
            }

            val launchFileObj = LaunchFile(version = version, configurations = configs)
            _launchConfigs.value = configs
            Log.i("TaskRepo", "Loaded ${configs.size} launch configs from ${launchFile.absolutePath}")
            launchFileObj
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to load launch.json from $workspacePath", e)
            null
        }
    }

    override suspend fun saveTasksFile(workspacePath: String, tasksFile: TasksFile): Boolean = withContext(Dispatchers.IO) {
        try {
            val vscodeDir = File(workspacePath, ".vscode")
            if (!vscodeDir.exists()) {
                vscodeDir.mkdirs()
            }

            val file = File(vscodeDir, "tasks.json")
            val json = JSONObject()
            json.put("version", tasksFile.version)
            val tasksArray = JSONArray()
            tasksFile.tasks.forEach { task ->
                val taskObj = JSONObject()
                taskObj.put("label", task.label)
                taskObj.put("type", task.type)
                taskObj.put("command", task.command)
                if (task.args.isNotEmpty()) {
                    val argsArray = JSONArray()
                    task.args.forEach { argsArray.put(it) }
                    taskObj.put("args", argsArray)
                }
                tasksArray.put(taskObj)
            }
            json.put("tasks", tasksArray)

            file.writeText(json.toString(4))
            _tasks.value = tasksFile.tasks
            Log.i("TaskRepo", "Saved ${tasksFile.tasks.size} tasks to ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to save tasks.json to $workspacePath", e)
            false
        }
    }

    override suspend fun saveLaunchFile(workspacePath: String, launchFile: LaunchFile): Boolean = withContext(Dispatchers.IO) {
        try {
            val vscodeDir = File(workspacePath, ".vscode")
            if (!vscodeDir.exists()) {
                vscodeDir.mkdirs()
            }

            val file = File(vscodeDir, "launch.json")
            val json = JSONObject()
            json.put("version", launchFile.version)
            val configsArray = JSONArray()
            launchFile.configurations.forEach { config ->
                val configObj = JSONObject()
                configObj.put("name", config.name)
                configObj.put("type", config.type)
                configObj.put("request", config.request)
                config.program?.let { configObj.put("program", it) }
                configsArray.put(configObj)
            }
            json.put("configurations", configsArray)

            file.writeText(json.toString(4))
            _launchConfigs.value = launchFile.configurations
            Log.i("TaskRepo", "Saved ${launchFile.configurations.size} launch configs to ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to save launch.json to $workspacePath", e)
            false
        }
    }

    override suspend fun executeTask(task: TaskDefinition, workspacePath: String): TaskExecutionResult = withContext(Dispatchers.IO) {
        try {
            Log.i("TaskRepo", "Executing task: ${task.label} command: ${task.command} ${task.args.joinToString(" ")} in $workspacePath")

            val workingDir = task.options.cwd?.let { File(it) } ?: File(workspacePath)
            val commandList = mutableListOf<String>()
            commandList.add(task.command)
            commandList.addAll(task.args)

            val processBuilder = ProcessBuilder(commandList)
                .directory(workingDir)
                .redirectErrorStream(false)

            // Set env
            if (task.options.env.isNotEmpty()) {
                processBuilder.environment().putAll(task.options.env)
            }

            val process = processBuilder.start()
            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            val result = TaskExecutionResult(
                success = exitCode == 0,
                exitCode = exitCode,
                output = output,
                error = if (error.isNotBlank()) error else null,
                taskLabel = task.label
            )

            Log.i("TaskRepo", "Task ${task.label} finished with exit code $exitCode")
            result
        } catch (e: Exception) {
            Log.e("TaskRepo", "Failed to execute task ${task.label}", e)
            TaskExecutionResult(
                success = false,
                exitCode = -1,
                output = "",
                error = e.message,
                taskLabel = task.label
            )
        }
    }
}
