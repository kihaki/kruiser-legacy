package de.gaw.kruiser.ui.singletopstack.transition

import android.util.Log
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val LocalEntryExitTransitionTracker =
    compositionLocalOf<EntryExitTransitionTracker> { error("No ExitTransitionTracker provided.") }

@Composable
fun rememberEntryExitTransitionTracker(
    navigationState: NavigationState,
    scope: CoroutineScope = rememberCoroutineScope(),
) = remember(scope, navigationState) {
    DefaultEntryExitTransitionTracker(
        navigationState = navigationState,
        scope = scope,
    )
}

@Composable
fun EntryExitTransitionTracker.collectCurrentExitTransition(): State<DestinationTransition?> =
    exitTransition.collectAsState()

@Composable
fun EntryExitTransitionTracker.collectCurrentEntryTransition(): State<DestinationTransition?> =
    entryTransition.collectAsState()

@Composable
fun EntryExitTransitionTracker.collectTransitionState(
    destination: Destination,
): State<MutableTransitionState<Boolean>> {
    val currentExitTransition by collectCurrentExitTransition()
    val currentEntryTransition by collectCurrentEntryTransition()

    return remember {
        derivedStateOf {
            when (destination) {
                currentEntryTransition?.destination -> currentEntryTransition?.transitionState
                currentExitTransition?.destination -> currentExitTransition?.transitionState
                else -> null
            } ?: MutableTransitionState(initialState = true)
        }
    }
}

interface EntryExitTransitionTracker {
    val exitTransition: StateFlow<DestinationTransition?>
    val entryTransition: StateFlow<DestinationTransition?>
}

class PreviewEntryExitTransitionTracker(
    current: DestinationTransition? = null,
) : EntryExitTransitionTracker {
    override val exitTransition: StateFlow<DestinationTransition?> = MutableStateFlow(current)
    override val entryTransition: StateFlow<DestinationTransition?> = MutableStateFlow(current)
}

class DefaultEntryExitTransitionTracker(
    scope: CoroutineScope,
    private val navigationState: NavigationState,
    private val animateInitialDestination: Boolean = false,
) : EntryExitTransitionTracker {
    override val exitTransition = MutableStateFlow<DestinationTransition?>(null)
    override val entryTransition = MutableStateFlow<DestinationTransition?>(null)

    private var previousStackSize: Int = 0
    private var previousTopDestination: Destination? = null

    init {
        scope.launch {
            navigationState.stack.collectLatest { stack ->
                try {
                    val requiresExitAnimation = stack.size < previousStackSize
                    val requiresEntryAnimation = stack.size > previousStackSize
                    if (requiresExitAnimation && previousTopDestination != null) {
                        previousTopDestination?.let { exitCandidate ->
                            exitCandidate
                                .withTransition(initialState = true)
                                .let { exitingDestination ->
                                    exitTransition.update { exitingDestination }
                                    with(exitingDestination.transitionState) {
                                        targetState = false
                                        awaitInvisible()
                                    }
                                }
                        }
                    } else if (requiresEntryAnimation) {
                        stack.lastOrNull()?.let { entryCandidate ->
                            // For nested navigation it might be good not to animate the initial destination
                            // so we won't if that is requested
                            val startVisible = !animateInitialDestination && stack.size == 1
                            entryCandidate
                                .withTransition(initialState = startVisible)
                                .let { enteringDestination ->
                                    entryTransition.update { enteringDestination }
                                    Log.v(
                                        "KruiserTransitions",
                                        "Entering Destination ${enteringDestination.destination}: ${enteringDestination.transitionState.currentState} -> ${enteringDestination.transitionState.targetState}"
                                    )
                                    with(enteringDestination.transitionState) {
                                        targetState = true
                                        awaitVisible()
                                    }
                                }
                        }
                    }
                } finally {
                    updateCachedStack(stack)
                    exitTransition.update { null }
                    entryTransition.update { null }
                }
            }
        }
    }

    private fun updateCachedStack(stack: List<Destination>) {
        previousTopDestination = stack.lastOrNull()
        previousStackSize = stack.size
    }

    private suspend fun MutableTransitionState<Boolean>.awaitInvisible() = awaitIsVisible(false)

    private suspend fun MutableTransitionState<Boolean>.awaitVisible() = awaitIsVisible(true)

    private suspend fun MutableTransitionState<Boolean>.awaitIsVisible(isVisible: Boolean) =
        combine(
            snapshotFlow { targetState },
            snapshotFlow { isIdle }
        ) { isCurrentlyVisible, isIdle -> isCurrentlyVisible to isIdle }
            .filter { (isCurrentlyVisible, isIdle) -> (isCurrentlyVisible == isVisible) && isIdle }
            .first() // Wait until the out animations have run
}

data class DestinationTransition(
    val destination: Destination,
    val transitionState: MutableTransitionState<Boolean>,
)

private fun Destination.withTransition(initialState: Boolean) = DestinationTransition(
    destination = this,
    transitionState = MutableTransitionState(initialState = initialState),
)