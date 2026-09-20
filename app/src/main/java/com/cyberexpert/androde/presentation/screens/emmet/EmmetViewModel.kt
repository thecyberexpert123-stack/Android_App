package com.cyberexpert.androde.presentation.screens.emmet

import androidx.lifecycle.ViewModel
import com.cyberexpert.androde.core.extensions.EmmetResult
import com.cyberexpert.androde.core.extensions.EmmetService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmmetViewModel @Inject constructor(
    private val emmetService: EmmetService
) : ViewModel() {

    suspend fun expandAbbreviation(abbreviation: String, languageId: String): EmmetResult {
        return emmetService.expandAbbreviation(abbreviation, languageId)
    }

    fun isEmmetAbbreviation(text: String): Boolean {
        return emmetService.isEmmetAbbreviation(text)
    }
}
