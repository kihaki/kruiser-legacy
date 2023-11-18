package de.gaw.kruiser.backstack.ui.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.ui.animation.util.LocalAnimationSyncedEntries
import de.gaw.kruiser.backstack.ui.util.collectEntries

/**
 * Renders the most recent screen without animations.
 */
@Composable
fun NoAnimation(
    backstack: Backstack,
) {
    val entriesState = backstack.collectEntries()
    val entries by entriesState
    val currentEntry = entries.last()
    CompositionLocalProvider(LocalAnimationSyncedEntries provides entriesState) {
        val screen = remember(currentEntry) { currentEntry.build() }
        screen.Content()
    }
}