package de.gaw.kruiser.backstack.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow

@Composable
fun rememberIsOnBackstack(
    entry: BackstackEntry = LocalBackstackEntry.currentOrThrow,
    backstack: Backstack = LocalBackstack.currentOrThrow,
): State<Boolean> {
    val currentEntry by rememberUpdatedState(entry)
    val entries by backstack.collectEntries()
    return remember {
        derivedStateOf { entries.contains(currentEntry) }
    }
}