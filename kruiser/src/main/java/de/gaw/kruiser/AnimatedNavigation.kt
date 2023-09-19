package de.gaw.kruiser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.collectIsEmpty
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.transition.DestinationTransition
import de.gaw.kruiser.transition.LocalExitTransitionTracker
import de.gaw.kruiser.transition.collectCurrentExitTransition
import de.gaw.kruiser.transition.rememberExitTransitionTracker

@Composable
fun AnimatedNavigation(
    modifier: Modifier = Modifier,
    state: NavigationState = LocalNavigationState.current,
) {
    val isEmpty by state.collectIsEmpty()

    BackHandler(
        enabled = !isEmpty,
        onBack = state::pop,
    )

    val stack by state.collectCurrentStack()
    val exitTransitionTracker = rememberExitTransitionTracker(navigationState = state)
    val exitTransition by exitTransitionTracker.collectCurrentExitTransition()

    // All screens on stack with additionally the exiting destination if it exists
    val screensToRender = remember(stack, exitTransition) {
        val screens = (stack + exitTransition?.destination)
            .filterNotNull()
            .map { destination -> destination.build() }
            .cullInvisible(exitTransition = exitTransition) // Cull invisible screens
            .mapIndexed { index, screen -> index to screen }

        screens
    }

    Box(modifier = modifier) {
        CompositionLocalProvider(
            LocalNavigationState provides state,
            LocalExitTransitionTracker provides exitTransitionTracker
        ) {
            val stateHolder: SaveableStateHolder = rememberSaveableStateHolder()
            screensToRender.forEach { (index, screen) ->
                key(screen.composeKey) {
                    // Render the screen at the appropriate zIndex of the stack
                    Box(modifier = Modifier.zIndex(index.toFloat())) {
                        // provide the screen with its own saved state
                        stateHolder.SaveableStateProvider(screen.savedStateKey) {
                            screen.Content()
                        }
                    }
                }
            }
        }
    }
}

private fun List<Screen>.cullInvisible(exitTransition: DestinationTransition?): List<Screen> {
    // Cull invisible screens
    val isScreenExiting = exitTransition != null
    val dropCount = when (isScreenExiting) {
        true -> size - 2 // Keep exiting and the one below
        false -> size - 2 // Keep exiting and the one below
    }.let { toDrop ->
        // Keep translucent screens if there are any
//        val translucentCountToKeep = take(toDrop) // Take all screens that should be dropped
//            .takeLastWhile { screen -> screen.isTranslucent } // From those, take the translucent ones on top
//            .size + 1 // and take their count + 1 (since the screen below the lowest translucent one also has to be kept)
        toDrop// - translucentCountToKeep
    }.coerceAtLeast(0)

    return drop(dropCount) // Drop all invisible screens
}