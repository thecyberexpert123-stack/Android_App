package com.cyberexpert.androde.presentation.components.ide

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import com.cyberexpert.androde.domain.model.ide.CommandAction

/**
 * Keybinding handler for Androde, similar to VS Code keybindings.
 * Handles external keyboard shortcuts like Ctrl+S, Ctrl+Shift+P, etc.
 * Optimized for Android with external keyboard support.
 */
@Composable
fun KeybindingHandler(
    onAction: (CommandAction) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false

                val action = when {
                    keyEvent.isCtrlPressed && keyEvent.isShiftPressed && keyEvent.key == Key.P -> CommandAction.COMMAND_PALETTE
                    keyEvent.isCtrlPressed && keyEvent.key == Key.S -> CommandAction.SAVE_FILE
                    keyEvent.isCtrlPressed && keyEvent.key == Key.W -> CommandAction.CLOSE_FILE
                    keyEvent.isCtrlPressed && keyEvent.key == Key.N -> CommandAction.NEW_FILE
                    keyEvent.isCtrlPressed && keyEvent.key == Key.O -> CommandAction.OPEN_FOLDER
                    keyEvent.isCtrlPressed && keyEvent.key == Key.F -> CommandAction.FIND
                    keyEvent.isCtrlPressed && keyEvent.isShiftPressed && keyEvent.key == Key.F -> CommandAction.SEARCH_IN_FILES
                    keyEvent.isCtrlPressed && keyEvent.key == Key.Grave -> CommandAction.TOGGLE_TERMINAL
                    keyEvent.isCtrlPressed && keyEvent.key == Key.Backslash -> CommandAction.SPLIT_EDITOR
                    else -> null
                }

                if (action != null) {
                    onAction(action)
                    true
                } else {
                    false
                }
            }
    ) {
        content()
    }
}
