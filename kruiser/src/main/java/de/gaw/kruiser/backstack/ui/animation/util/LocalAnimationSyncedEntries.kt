package de.gaw.kruiser.backstack.ui.animation.util

import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import de.gaw.kruiser.backstack.ImmutableEntries

/**
 * Currently destinations, synchronized with the ui animations so we can listen to screens being
 * visible/invisible.
 */
val LocalAnimationSyncedEntries =
    compositionLocalOf<State<ImmutableEntries>> { error("No animation synced entries provided.") }