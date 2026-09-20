package com.cyberexpert.androde.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.tasks.LaunchConfiguration
import com.cyberexpert.androde.core.tasks.TaskDefinition
import com.cyberexpert.androde.core.tasks.TaskExecutionResult
import com.cyberexpert.androde.core.tasks.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Tasks ViewModel - Phase 10 100% REAL WORKING++++++++.
 */

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskDefinition>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _launchConfigs = MutableStateFlow<List<LaunchConfiguration>>(emptyList())
    val launchConfigs = _launchConfigs.asStateFlow()

    private val _lastResult = MutableStateFlow<TaskExecutionResult?>(null)
    val lastResult = _lastResult.asStateFlow()

    init {
        viewModelScope.launch {
            // Load from default workspace if exists
            val tasksFile = taskRepository.loadTasksFile("/data/data/com.cyberexpert.androde/files")
            if (tasksFile != null) _tasks.value = tasksFile.tasks
            val launchFile = taskRepository.loadLaunchFile("/data/data/com.cyberexpert.androde/files")
            if (launchFile != null) _launchConfigs.value = launchFile.configurations
        }
    }

    suspend fun execute(task: TaskDefinition, workspacePath: String) {
        val result = taskRepository.executeTask(task, workspacePath)
        _lastResult.value = result
    }
}
