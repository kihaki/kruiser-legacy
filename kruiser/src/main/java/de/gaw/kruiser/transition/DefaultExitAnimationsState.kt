package de.gaw.kruiser.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.currentStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@Composable
fun rememberExitAnimationsRenderState(
    navigationState: NavigationState = LocalNavigationState.current,
): ExitAnimationsState {
    val scope = rememberCoroutineScope()
    return remember(navigationState, scope) {
        DefaultExitAnimationsState(
            scope = scope,
            navState = navigationState,
        )
    }
}

class DefaultExitAnimationsState(
    scope: CoroutineScope,
    private val navState: NavigationState,
) : ExitAnimationsState {
    override val onScreenDestinations = MutableStateFlow(navState.currentStack)
    override val destinationsRunningExitAnimations = MutableStateFlow<Set<Destination>>(emptySet())

    private var destinationsSupportingAnimation = emptySet<Destination>()

    init {
        scope.launch {
            navState.stack.collectLatest { stackDestinations ->
                val previousDestinations = onScreenDestinations.value
                val destinationsThatWillRunRemoveAnimations = previousDestinations
                    .asSequence()
                    .drop(stackDestinations.size)
                    .filter { destination -> destinationsSupportingAnimation.contains(destination) }
                    .filterNot { destination -> stackDestinations.contains(destination) } // Find removed destinations
                    .toSet()

                destinationsRunningExitAnimations.update {
                    destinationsThatWillRunRemoveAnimations
                }

                onScreenDestinations.update {
                    stackDestinations + destinationsThatWillRunRemoveAnimations
                }
            }
        }
    }

    override fun onExitAnimationCompleted(destination: Destination) {
        destinationsRunningExitAnimations.update { destinations ->
            destinations - destination
        }
        onScreenDestinations.update { destinations ->
            destinations - destination
        }
    }

    override fun registerExitAnimationSupport(destination: Destination) {
        destinationsSupportingAnimation += destination
    }

    override fun unregisterExitAnimationSupport(destination: Destination) {
        destinationsSupportingAnimation -= destination
    }
}