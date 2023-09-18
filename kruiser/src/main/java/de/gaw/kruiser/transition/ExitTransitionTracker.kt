package de.gaw.kruiser.transition

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

val LocalExitTransitionTracker =
    compositionLocalOf<ExitTransitionTracker> { error("No ExitTransitionTracker provided.") }

@Composable
fun rememberExitTransitionTracker(
    navigationState: NavigationState,
    scope: CoroutineScope = rememberCoroutineScope(),
) = remember(scope, navigationState) {
    DefaultExitTransitionTracker(
        navigationState = navigationState,
        scope = scope,
    )
}

@Composable
fun ExitTransitionTracker.collectCurrentExitTransition(): State<DestinationTransition?> =
    exitTransition.collectAsState()

@Composable
fun ExitTransitionTracker.collectTransitionState(destination: Destination): State<MutableTransitionState<Boolean>> {
    val currentExitTransition by collectCurrentExitTransition()
    val initialTransition = remember {
        MutableTransitionState(initialState = false)
            .apply { targetState = true }
    }
    return remember {
        derivedStateOf {
            val transitionState = currentExitTransition
                ?.takeIf { it.destination == destination }
                ?.transitionState
                ?: initialTransition
            transitionState
        }
    }
}

interface ExitTransitionTracker {
    val exitTransition: StateFlow<DestinationTransition?>
}

class PreviewExitTransitionTracker(
    current: DestinationTransition? = null,
) : ExitTransitionTracker {
    override val exitTransition: StateFlow<DestinationTransition?> = MutableStateFlow(current)
}

class DefaultExitTransitionTracker(
    scope: CoroutineScope,
    private val navigationState: NavigationState,
) : ExitTransitionTracker{
    override val exitTransition = MutableStateFlow<DestinationTransition?>(null)

    private var previousStackSize: Int = 0
    private var previousTopDestination: Destination? = null

    init {
        scope.launch {
            navigationState.stack.collectLatest { stack ->
                try {
                    previousTopDestination?.let { exitCandidate ->
                        val requiresExitAnimation = stack.size < previousStackSize

                        if (requiresExitAnimation) {
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
                    }
                } finally {
                    updateCachedStack(stack)
                    exitTransition.update { null }
                }
            }
        }
    }

    private fun updateCachedStack(stack: List<Destination>) {
        previousTopDestination = stack.lastOrNull()
        previousStackSize = stack.size
    }

    private suspend fun MutableTransitionState<Boolean>.awaitInvisible() =
        combine(
            snapshotFlow { targetState },
            snapshotFlow { isIdle }
        ) { isVisible, isIdle -> isVisible to isIdle }
            .filter { (isVisible, isIdle) -> !isVisible && isIdle }
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