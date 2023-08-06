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
import de.gaw.kruiser.state.currentStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val LocalAnimatedNavigationState =
    compositionLocalOf<AnimatedNavigationState> { error("No AnimatedNavigationState provided.") }

@Composable
fun rememberAnimatedNavigationState(
    navigationState: NavigationState,
    scope: CoroutineScope = rememberCoroutineScope(),
) = remember(scope, navigationState) {
    AnimatedNavigationState(
        sourceNavigationState = navigationState,
        scope = scope,
    )
}

@Composable
fun AnimatedNavigationState.collectStack(): State<List<DestinationTransition>> =
    stack.collectAsState()

@Composable
fun AnimatedNavigationState.collectTransitionState(destination: Destination): State<MutableTransitionState<Boolean>> {
    val stack by collectStack()
    return remember {
        derivedStateOf {
            stack.firstOrNull { (currentDestination, _) -> currentDestination == destination }
                ?.transitionState ?: MutableTransitionState(initialState = false)
        }
    }
}

class AnimatedNavigationState(
    scope: CoroutineScope,
    private val sourceNavigationState: NavigationState,
) {
    val stack = MutableStateFlow(
        sourceNavigationState.currentStack.map { destination ->
            destination.withTransition(initialState = true)
        },
    )

    init {
        scope.launch {
            sourceNavigationState.stack.collectLatest { newDestinations ->
                val currentDestinations = stack.value
                when {
                    currentDestinations.size > newDestinations.size -> {
                        val exitingDestination = currentDestinations.last()
                        val newDestinationsWithTransition = newDestinations.map { destination ->
                            destination.withTransition(initialState = true)
                        }
                        stack.update {
                            newDestinationsWithTransition + exitingDestination
                        }
                        playExitTransition(
                            destinationToRemove = currentDestinations.last(),
                        )
                        stack.update {
                            newDestinationsWithTransition
                        }
                    }

                    currentDestinations.size < newDestinations.size -> stack.update {
                        newDestinations
                            .take(currentDestinations.size)
                            .map { destination ->
                                destination.withTransition(initialState = true)
                            } + newDestinations
                            .drop(currentDestinations.size)
                            .map { destination ->
                                destination.withTransition(initialState = false).apply {
                                    transitionState.targetState = true
                                }
                            }
                    }

                    else -> stack.update {
                        newDestinations.map { destination ->
                            destination.withTransition(initialState = true)
                        }
                    }
                }
            }
        }
    }


    private suspend fun playExitTransition(
        destinationToRemove: DestinationTransition,
    ) = destinationToRemove.let { (_, transition) ->
        combine(
            snapshotFlow { transition.targetState }
                .onStart { emit(transition.targetState) },
            snapshotFlow { transition.isIdle }
                .onStart { emit(transition.isIdle) }
        ) { isVisible, isIdle -> isVisible to isIdle }
            .filter { (isVisible, isIdle) -> !isVisible && isIdle }
            .onStart { transition.targetState = false }
            .first() // Wait until the out animations have run
    }

}

data class DestinationTransition(
    val destination: Destination,
    val transitionState: MutableTransitionState<Boolean>,
)

private fun Destination.withTransition(initialState: Boolean) = DestinationTransition(
    destination = this,
    transitionState = MutableTransitionState(initialState = initialState),
)