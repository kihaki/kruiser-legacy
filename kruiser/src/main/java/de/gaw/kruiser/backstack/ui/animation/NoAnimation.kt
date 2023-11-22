package de.gaw.kruiser.backstack.ui.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.ui.ScreenContent
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.collectEntries

/**
 * Renders the most recent screen without animations.
 */
@Composable
fun NoAnimation(
    backstack: Backstack = LocalBackstack.current,
) {
    val entriesState = backstack.collectEntries()
    val entries by entriesState
    val currentEntry = entries.last()
    ScreenContent(currentEntry)
}