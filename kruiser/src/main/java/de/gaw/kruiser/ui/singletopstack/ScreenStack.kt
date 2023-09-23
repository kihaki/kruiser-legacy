package de.gaw.kruiser.ui.singletopstack

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.ui.PopOnBackHandler
import de.gaw.kruiser.ui.singletopstack.transition.EntryExitTransitionStateTracker
import de.gaw.kruiser.ui.singletopstack.transition.HorizontalCardStackTransition
import de.gaw.kruiser.ui.singletopstack.transition.LocalEntryExitTransitionStateTracker
import de.gaw.kruiser.ui.singletopstack.transition.collectCurrentExitTransition
import de.gaw.kruiser.ui.singletopstack.transition.rememberEntryExitTransitionStateTracker
import kotlinx.coroutines.flow.collectLatest

typealias ScreenStackRenderer = @Composable ScreenStackRenderContext.() -> Unit

private val noAnimationScreenRenderer: ScreenStackRenderer = { screen.Content() }

val horizontalTransitionRenderer: ScreenStackRenderer = {
    HorizontalCardStackTransition {
        screen.Content()
    }
}

/**
 * A composable to render the current destinations as stacked screens,
 * aka default Android behavior on phones.
 * @param drawScreen: Defines how to render the screens,
 * this allows to wrap the screen in transition animations
 */
@Composable
fun ScreenStack(
    modifier: Modifier = Modifier,
    state: NavigationState = LocalNavigationState.current,
    scopedServiceProvider: ScopedServiceProvider = LocalScopedServiceProvider.current,
    drawScreen: ScreenStackRenderer = horizontalTransitionRenderer,
) {
    CompositionLocalProvider(
        LocalNavigationState provides state,
    ) {
        // Pop destinations off the stack on back press
        PopOnBackHandler()

        Box(modifier = modifier) {
            // Keep track of running screen transition states
            val transitionStateTracker =
                rememberEntryExitTransitionStateTracker(navigationState = state)
            CompositionLocalProvider(
                LocalEntryExitTransitionStateTracker provides transitionStateTracker
            ) {
                // Clear dead services based on the destinations that are on the ui.
                // This prevents clearing services for screens before they completed
                // their exit animations.
                val stackDestinations by rememberScreenStackDestinations()
                LaunchedEffect(Unit) {
                    snapshotFlow { stackDestinations }.collectLatest { destinations ->
                        scopedServiceProvider.clearDeadServices(destinations)
                    }
                }

                // Filter for those screens that actually need to be rendered.
                // This filters screens that are not visible at this moment,
                // such as those that currently are deep down in the stack.
                val visibleDestinations by rememberScreenStackVisibleDestinations()

                val stateHolder: SaveableStateHolder = rememberSaveableStateHolder()

                visibleDestinations.forEachIndexed { zIndex, destination ->
                    key(destination) {
                        val context by rememberScreenStackRenderContext(
                            destination = destination,
                            transitionStateTracker = transitionStateTracker,
                        )
                        stateHolder.SaveableStateProvider(context.screen.savedStateKey) {
                            Box(modifier = Modifier.zIndex(zIndex.toFloat())) {
                                context.drawScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Will return all destinations that are on the stack, including the one animating out
 * (which is still in the UI but not in the navigation state).
 */
@Composable
fun rememberScreenStackDestinations(
    state: NavigationState = LocalNavigationState.current,
    transitionTracker: EntryExitTransitionStateTracker = LocalEntryExitTransitionStateTracker.current,
): State<List<Destination>> {
    val stack by state.collectCurrentStack()
    val exitTransition by transitionTracker.collectCurrentExitTransition()
    return remember {
        derivedStateOf {
            (stack + exitTransition?.destination)
                .filterNotNull()
        }
    }
}