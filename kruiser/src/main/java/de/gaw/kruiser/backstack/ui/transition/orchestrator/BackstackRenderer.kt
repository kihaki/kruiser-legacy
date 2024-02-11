package de.gaw.kruiser.backstack.ui.transition.orchestrator

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.BackstackEntry
import de.gaw.kruiser.backstack.ui.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.Render
import de.gaw.kruiser.backstack.ui.transition.orchestrator.ScreenTransitionState.Companion.fromTransition
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

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
        val stackToRender = rememberScreenTransitionsBackstack(backstack)

        CompositionLocalProvider(LocalScreenTransitionBackstack provides stackToRender) {
            val entriesToRender by stackToRender.collectEntries()

            entriesToRender.fastForEach { entry ->
                key(entry.id) { // Key is important here! TODO: Check why.
                    entry.Render()
                }
            }
        }
    }
}

@Composable
fun BackstackEntry.rememberScreen(): Screen = remember(id) {
    destination.build()
}

/**
 * Syncs the [ScreenTransitionTracker] with the state of this [AnimatedVisibilityScope].
 * Tracking the entry/exit animations in the [ScreenTransitionTracker].
 */
@Composable
@OptIn(ExperimentalAnimationApi::class)
fun AnimatedVisibilityScope.UpdateTransitionStateEffect(
    entry: BackstackEntry = LocalBackstackEntry.current,
    transitionTracker: ScreenTransitionTracker = LocalScreenTransitionBackstack.currentOrThrow,
) {
    LaunchedEffect(Unit) {
        combine(
            snapshotFlow { transition.currentState },
            snapshotFlow { transition.targetState },
        ) { cur, tar -> cur to tar }
            .collectLatest { (currentState, targetState) ->
                transitionTracker.updateTransitionState(
                    entry = entry,
                    transitionState = fromTransition(currentState, targetState),
                )
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            transitionTracker.onDisposeFromComposition(entry)
        }
    }
}

