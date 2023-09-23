package de.gaw.kruiser.ui.singletopstack

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.ui.singletopstack.transition.EntryExitTransitionStateTracker
import de.gaw.kruiser.ui.singletopstack.transition.collectTransitionState

interface ScreenStackRenderContext {
    val screen: Screen
    val transitionState: MutableTransitionState<Boolean>
}

private data class DefaultScreenStackRenderContext(
    override val screen: Screen,
    override val transitionState: MutableTransitionState<Boolean>,
) : ScreenStackRenderContext

@Composable
internal fun rememberScreenStackRenderContext(
    destination: Destination,
    transitionStateTracker: EntryExitTransitionStateTracker,
): State<ScreenStackRenderContext> {
    val screen = remember { destination.build() }
    val transitionState by transitionStateTracker.collectTransitionState(destination)
    return remember {
        derivedStateOf {
            DefaultScreenStackRenderContext(
                screen = screen,
                transitionState = transitionState,
            )
        }
    }
}