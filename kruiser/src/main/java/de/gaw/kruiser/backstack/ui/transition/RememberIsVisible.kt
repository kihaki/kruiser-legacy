package de.gaw.kruiser.backstack.ui.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.util.LocalBackstackState
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow

/**
 * Returns true if [entry] should be visible, can be used to sync [AnimatedVisibility] with the
 * [ScreenTransitionTracker] to sync animations with the ui backstack.
 */
@Composable
fun rememberIsVisible(
    entry: BackstackEntry = LocalBackstackEntry.currentOrThrow,
    backstack: BackstackState = LocalBackstackState.currentOrThrow,
    transitionTracker: ScreenTransitionTracker = LocalScreenTransitionTracker.currentOrThrow,
): MutableState<Boolean> {
    val isVisible = remember {
        val isAnimatedIn = transitionTracker.transitionState(entry)?.let {
            it == ScreenTransitionState.EnterTransitionRunning || it == ScreenTransitionState.EntryTransitionDone
        } ?: false
        mutableStateOf(isAnimatedIn)
    }

    val backstackEntries by backstack.collectEntries()
    LaunchedEffect(backstackEntries, backstack, entry) {
        isVisible.value = backstackEntries.contains(entry)
    }

    return isVisible
}