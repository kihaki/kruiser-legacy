package de.gaw.kruiser.backstack.ui.rendering

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.transition.LocalScreenTransitionTracker
import de.gaw.kruiser.backstack.ui.util.collectEntries

/**
 * Decides which [BackstackEntry]s to render and orchestrates transitions.
 */
@Composable
fun BackstackRenderer(
    backstack: BackstackState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        /**
         * Contains [BackstackEntry]s that are animating in/out or are currently visible on screen.
         */
        val onScreenBackstack = rememberOnScreenBackstack(backstack)

        CompositionLocalProvider(
            LocalOnScreenBackstack provides onScreenBackstack,
            LocalScreenTransitionTracker provides onScreenBackstack,
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

