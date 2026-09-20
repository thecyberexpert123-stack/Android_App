package com.cyberexpert.androde.core.keybinding

import com.cyberexpert.androde.domain.model.ide.Keybinding
import kotlinx.coroutines.flow.Flow

interface KeybindingRepository {
    fun getKeybindings(): Flow<List<Keybinding>>
    suspend fun setKeybinding(keybinding: Keybinding)
    suspend fun removeKeybinding(id: String)
    fun getDefaultKeybindings(): List<Keybinding>
}
