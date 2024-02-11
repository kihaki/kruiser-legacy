package de.gaw.kruiser.backstack.ui.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshotFlow
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.LocalOnScreenBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.Screen
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine

val LocalScreenTransitionTracker = compositionLocalOf<ScreenTransitionTracker?> { null }

interface ScreenTransitionTracker {
    val transitionStates: StateFlow<Map<BackstackEntry, ScreenTransitionState>>
    fun updateTransitionState(entry: BackstackEntry, transitionState: ScreenTransitionState)
    fun onDisposeFromComposition(entry: BackstackEntry)
}

/**
 * Describes the state of a [Screen]s transition.
 */
enum class ScreenTransitionState {
    EnterTransitionRunning,
    EntryTransitionDone,
    ExitTransitionRunning,
    ExitTransitionDone;

    companion object {
        @OptIn(ExperimentalAnimationApi::class)
        fun fromTransition(currentState: EnterExitState, targetState: EnterExitState) = when {
            currentState != targetState -> when (targetState) {
                EnterExitState.Visible -> EnterTransitionRunning
                else -> ExitTransitionRunning
            }

            else -> when (targetState) {
                EnterExitState.Visible -> EntryTransitionDone
                else -> ExitTransitionDone
            }
        }
    }
}

fun ScreenTransitionTracker.transitionState(entry: BackstackEntry) = transitionStates.value[entry]

/**
 * Syncs the [ScreenTransitionTracker] with the state of this [AnimatedVisibilityScope].
 * Tracking the entry/exit animations in the [ScreenTransitionTracker].
 */
@Composable
@OptIn(ExperimentalAnimationApi::class)
fun AnimatedVisibilityScope.UpdateTransitionStateEffect(
    entry: BackstackEntry = LocalBackstackEntry.currentOrThrow,
    transitionTracker: ScreenTransitionTracker = LocalOnScreenBackstack.currentOrThrow,
) {
    val transitionState = produceState<ScreenTransitionState?>(null) {
        combine(
            snapshotFlow { transition.currentState },
            snapshotFlow { transition.targetState },
        ) { cur, tar -> cur to tar }
            .collectLatest { (currentState, targetState) ->
                value = ScreenTransitionState.fromTransition(currentState, targetState)
            }
    }
    UpdateTransitionStateEffect(
        state = transitionState,
        entry = entry,
        transitionTracker = transitionTracker,
    )
}

/**
 * Syncs the [ScreenTransitionTracker] with the provided [state].
 */
@Composable
fun UpdateTransitionStateEffect(
    state: State<ScreenTransitionState?>,
    entry: BackstackEntry = LocalBackstackEntry.currentOrThrow,
    transitionTracker: ScreenTransitionTracker = LocalOnScreenBackstack.currentOrThrow,
) {
    val currentState by state
    LaunchedEffect(currentState) {
        currentState?.let {
            transitionTracker.updateTransitionState(
                entry = entry,
                transitionState = it,
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            transitionTracker.onDisposeFromComposition(entry)
        }
    }
}