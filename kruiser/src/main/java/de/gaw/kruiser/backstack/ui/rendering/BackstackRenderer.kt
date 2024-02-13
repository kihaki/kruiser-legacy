package de.gaw.kruiser.backstack.ui.rendering

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.transition.LocalScreenTransitionTracker
import de.gaw.kruiser.backstack.ui.transparency.LocalTransparencyState
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.LocalSaveableStateHolder
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.backstack.util.rememberScreen
import de.gaw.kruiser.viewmodel.viewModelStoreOwner

/**
 * Decides which [BackstackEntry]s to render and orchestrates transitions.
 */
@Composable
fun BackstackRenderer(
    modifier: Modifier = Modifier,
    backstack: Backstack = LocalBackstack.currentOrThrow,
) {
    Box(modifier = modifier) {
        /**
         * Contains [BackstackEntry]s that are animating in/out or are currently visible on screen.
         */
        val onScreenBackstack = rememberOnScreenBackstack(backstack)

        CompositionLocalProvider(
            LocalOnScreenBackstack provides onScreenBackstack,
            LocalScreenTransitionTracker provides onScreenBackstack,
            LocalTransparencyState provides onScreenBackstack,
        ) {
            val onScreenEntries by onScreenBackstack.collectEntries()

            onScreenEntries.fastForEach { entry ->
                key(entry.id) { // Key is important here! TODO: Check why.
                    entry.Render()
                }
            }
        }
    }
}

@Composable
fun BackstackEntry.Render(
    stateHolder: SaveableStateHolder = LocalSaveableStateHolder.currentOrThrow,
) {
    CompositionLocalProvider(LocalBackstackEntry provides this) {
        stateHolder.SaveableStateProvider(id) {
            val backstack = LocalBackstack.currentOrThrow
            val entryViewModelStoreOwner = backstack.viewModelStoreOwner(this)
            CompositionLocalProvider(LocalViewModelStoreOwner provides entryViewModelStoreOwner) {
                val screen = rememberScreen()
                screen.Content()
            }
        }
    }
}

