package de.gaw.kruiser.backstack.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.LocalOnScreenBackstack
import de.gaw.kruiser.backstack.ui.rendering.OnScreenBackstack
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow

@Composable
fun rememberIsInBack(
    entry: BackstackEntry = LocalBackstackEntry.currentOrThrow,
    backstack: Backstack = LocalBackstack.currentOrThrow,
    onScreenBackstack: OnScreenBackstack = LocalOnScreenBackstack.currentOrThrow,
): State<Boolean> {
    val entries by backstack.collectEntries()
    val screenEntries by onScreenBackstack.collectEntries()
    val isTop = entries.lastOrNull() == entry
    val isScreenTop = screenEntries.lastOrNull() == entry

    val isPop = screenEntries.size > entries.size
    val isPopping = isScreenTop && isPop

    val isInBack = !isTop && !isPopping
    val inBack = remember {
        mutableStateOf(isPop)
    }
    LaunchedEffect(isInBack) {
        inBack.value = isInBack
    }

    return inBack
}