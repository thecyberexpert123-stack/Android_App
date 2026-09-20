package com.cyberexpert.androde.presentation.screens.formatting

import androidx.lifecycle.ViewModel
import com.cyberexpert.androde.core.extensions.FormattingRepository
import com.cyberexpert.androde.core.extensions.FormattingResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FormattingViewModel @Inject constructor(
    private val formattingRepository: FormattingRepository
) : ViewModel() {

    suspend fun formatDocument(content: String, languageId: String, tabSize: Int = 4, insertSpaces: Boolean = true): FormattingResult {
        return formattingRepository.formatDocument(content, languageId, tabSize, insertSpaces)
    }
}
