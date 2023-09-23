package de.gaw.kruiser.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder

/**
 * Will provide the [content] with a saved state based on the [Screen].
 */
@Composable
fun WithScreenSavedState(
    screen: Screen,
    content: @Composable () -> Unit,
) {
    val stateHolder: SaveableStateHolder = rememberSaveableStateHolder()
    key(screen.composeKey) {
        stateHolder.SaveableStateProvider(screen.savedStateKey) {
            content()
        }
    }
}