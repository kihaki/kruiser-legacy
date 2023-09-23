package de.gaw.kruiser.ui.singletopstack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.ui.singletopstack.transition.EntryExitTransitionStateTracker
import de.gaw.kruiser.ui.singletopstack.transition.LocalEntryExitTransitionStateTracker
import de.gaw.kruiser.ui.singletopstack.transition.collectCurrentEntryTransition
import de.gaw.kruiser.ui.singletopstack.transition.collectCurrentExitTransition

/**
 * Returns all destinations that are visible on the screen stack.
 */
@Composable
fun rememberScreenStackVisibleDestinations(
    state: NavigationState = LocalNavigationState.current,
    transitionTracker: EntryExitTransitionStateTracker = LocalEntryExitTransitionStateTracker.current,
): State<List<Destination>> {
    val stack by rememberScreenStackDestinations(
        state = state,
        transitionTracker = transitionTracker,
    )

    val isAnimating by rememberIsScreenTransitionRunning(transitionTracker = transitionTracker)

    return remember {
        derivedStateOf {
            stack.filterIsVisible(isAnimating)
        }
    }
}

/**
 * Returns true if there are either enter or exit transitions currently running
 */
@Composable
private fun rememberIsScreenTransitionRunning(
    transitionTracker: EntryExitTransitionStateTracker = LocalEntryExitTransitionStateTracker.current,
): State<Boolean> {
    val exitTransition by transitionTracker.collectCurrentExitTransition()
    val entryTransition by transitionTracker.collectCurrentEntryTransition()
    return remember { derivedStateOf { entryTransition != null || exitTransition != null } }
}

private fun List<Destination>.filterIsVisible(isAnimating: Boolean): List<Destination> {
    val visibleScreensCount = when (isAnimating) {
        true -> 2 // Keep top screen and the one below if animating
        false -> 1 // Keep the top screen only if the animations are not running
    }
    return takeLast(visibleScreensCount)
}

