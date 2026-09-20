package com.cyberexpert.androde.presentation.screens.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.filesystem.FileSystemRepository
import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.domain.model.ide.FileNode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileExplorerViewModel @Inject constructor(
    private val fileSystemRepository: FileSystemRepository
) : ViewModel() {

    private val _files = MutableStateFlow<List<FileNode>>(emptyList())
    val files: StateFlow<List<FileNode>> = _files.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadDirectory(directory: File, showHidden: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = fileSystemRepository.listFiles(directory, showHidden)) {
                is AppResult.Success -> _files.value = result.data
                is AppResult.Error -> {} // handle error
                else -> {}
            }
            _isLoading.value = false
        }
    }
}
