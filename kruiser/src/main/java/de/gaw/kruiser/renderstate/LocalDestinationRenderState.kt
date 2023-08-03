package de.gaw.kruiser.renderstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

val LocalDestinationRenderState =
    compositionLocalOf<DestinationRenderState> { error("No DestinationRenderState provided.") }

@Composable
fun rememberDestinationRenderState(navigationState: NavigationState): DestinationRenderState {
    val destinationRenderState = remember(navigationState) {
        DefaultDestinationRenderState()
    }

    val stack by navigationState.collectCurrentStack()
    LaunchedEffect(stack) {
        destinationRenderState.onStackUpdated(stack)
    }

    return destinationRenderState
}

@Composable
fun DestinationRenderState.collectVisibleDestinations() = visibleDestinations.collectAsState()

@Composable
fun DestinationRenderState.collectDestinationsToRenderInOrder(): State<List<Destination>> {
    val destinations by visibleDestinations.collectAsState()
    return remember {
        derivedStateOf {
            destinations
                .map { (destination, index) ->
                    index to destination
                }
                .sortedBy { (context, _) -> context.zIndex }
                .map { (_, destination) -> destination }
        }
    }
}

interface DestinationRenderState {
    val visibleDestinations: StateFlow<Map<Destination, DestinationRenderContext>>
    fun onAnimatedOut(destination: Destination)
    fun onStackUpdated(destinations: List<Destination>)
}

class DefaultDestinationRenderState : DestinationRenderState {
    override val visibleDestinations = MutableStateFlow(
        emptyMap<Destination, DestinationRenderContext>()
    )

    override fun onAnimatedOut(destination: Destination) {
        visibleDestinations.update { currentDestinations ->
            currentDestinations - destination
        }
    }

    override fun onStackUpdated(destinations: List<Destination>) {
        visibleDestinations.update { currentDestinations ->
            currentDestinations + destinations.mapIndexed { index, destination ->
                destination to DestinationRenderContext(zIndex = index.toFloat())
            }
        }
    }
}

data class DestinationRenderContext(
    val zIndex: Float,
)