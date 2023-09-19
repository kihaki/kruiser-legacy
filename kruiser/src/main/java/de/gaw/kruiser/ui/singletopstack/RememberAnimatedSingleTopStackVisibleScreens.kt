package de.gaw.kruiser.ui.singletopstack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.ui.singletopstack.transition.ExitTransitionTracker
import de.gaw.kruiser.ui.singletopstack.transition.LocalExitTransitionTracker
import de.gaw.kruiser.ui.singletopstack.transition.collectCurrentExitTransition

@Composable
fun rememberAnimatedSingleTopStackVisibleScreens(
    state: NavigationState = LocalNavigationState.current,
    exitTransitionTracker: ExitTransitionTracker = LocalExitTransitionTracker.current,
): State<List<AnimatedSingleTopStackScreen>> {
    // Collect the current navigation stack
    val stack by state.collectCurrentStack()

    // Keep track of screens that are removed from the stack but still need to be animated out
    val exitTransition by exitTransitionTracker.collectCurrentExitTransition()

    // All screens on stack with additionally the exiting destination if it exists
    return remember {
        derivedStateOf {
            val screens = (stack + exitTransition?.destination)
                .filterNotNull()
                .buildScreens()
                .takeVisibleScreens(isAnimating = exitTransition != null)
                .mapIndexed { index, screen ->
                    AnimatedSingleTopStackScreen(
                        zIndex = index.toFloat(),
                        screen = screen,
                    )
                }
            screens
        }
    }
}

private fun List<Destination>.buildScreens() = map { it.build() }

private fun List<Screen>.takeVisibleScreens(isAnimating: Boolean): List<Screen> {
    val visibleScreensCount = when (isAnimating) {
        true -> 2 // Keep top screen and the one below if animating
        false -> 1 // Keep the top screen only if the animations are not running
    }
    return takeLast(visibleScreensCount)
}