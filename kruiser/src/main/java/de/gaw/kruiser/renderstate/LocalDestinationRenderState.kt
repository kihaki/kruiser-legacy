package de.gaw.kruiser.renderstate

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.NavigationState.Event.Idle
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.NavigationState.Event.Push
import de.gaw.kruiser.state.NavigationState.Event.Replace
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.currentStack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

val LocalDestinationRenderState =
    compositionLocalOf<DestinationRenderState> { error("No DestinationRenderState provided.") }

@Composable
fun rememberDestinationRenderState(navigationState: NavigationState): DestinationRenderState {
    val destinationRenderState = remember(navigationState) {
        DefaultDestinationRenderState(navigationState)
    }

    val stack by navigationState.collectCurrentStack()
    LaunchedEffect(stack) {
        destinationRenderState.onStackUpdated(stack)
    }

    return destinationRenderState
}

@Composable
fun DestinationRenderState.collectVisibleDestinations() =
    destinationsRenderContexts.collectAsState()

@Composable
fun DestinationRenderState.collectDestinationsToRender(): State<List<Destination>> =
    destinations.collectAsState(emptyList())

interface DestinationRenderState {
    val destinationsRenderContexts: StateFlow<Map<Destination, DestinationRenderContext>>
    val destinations: Flow<List<Destination>>

    fun onAnimatedOut(destination: Destination)
    fun onStackUpdated(destinations: List<Destination>)
}

class DefaultDestinationRenderState(
    navState: NavigationState,
) : DestinationRenderState {
    override val destinationsRenderContexts = MutableStateFlow(
        emptyMap<Destination, DestinationRenderContext>()
    )

    init {
        onStackUpdated(navState.currentStack)
    }

    /**
     * For now we return the destination on top of the stack and the one animating.
     * If they are the same, we only return it once.
     */
    override val destinations = combine(
        navState.stack,
        navState.lastEvent,
        destinationsRenderContexts
    ) { stack, lastEvent, contexts -> Triple(stack, lastEvent, contexts) }
        .map { (stack, lastEvent, destinations) ->
            val currentDestination = when (lastEvent) {
                Idle,
                Push,
                Replace,
                -> stack.dropLast(1).lastOrNull()?.let { currentNavDestination ->
                    currentNavDestination to destinations.getOrDefault(
                        currentNavDestination,
                        DestinationRenderContext(0f)
                    )
                }

                Pop,
                -> stack.lastOrNull()?.let { currentNavDestination ->
                    currentNavDestination to destinations.getOrDefault(
                        currentNavDestination,
                        DestinationRenderContext(0f)
                    )
                }
            }

            val animatingDestination = destinations
                .maxByOrNull { (_, context) -> context.zIndex }
                ?.toPair()

            val renderDestinations = setOfNotNull(currentDestination, animatingDestination)
                .toList()
                .sortedBy { (_, context) -> context.zIndex }
                .map { (destination, _) -> destination }

            Log.v("AnimStack", "=> RenderDestinations:")
            renderDestinations.forEachIndexed { index, destination ->
                Log.v("AnimStack", "$index $destination")
            }

            renderDestinations
        }

    override fun onAnimatedOut(destination: Destination) {
        destinationsRenderContexts.update { currentDestinations ->
            currentDestinations - destination
        }
    }

    override fun onStackUpdated(destinations: List<Destination>) {
        destinationsRenderContexts.update { currentDestinations ->
            currentDestinations + destinations.mapIndexed { index, destination ->
                destination to DestinationRenderContext(zIndex = index.toFloat())
            }
        }
    }
}

data class DestinationRenderContext(
    val zIndex: Float,
)