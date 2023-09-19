package de.gaw.kruiser.ui.singletopstack

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.ui.singletopstack.transition.LocalExitTransitionTracker
import de.gaw.kruiser.ui.singletopstack.transition.rememberEntryExitTransitionTracker
import de.gaw.kruiser.ui.PopOnBackHandler

/**
 * A composable to render the current destinations as stacked screens,
 * aka default Android behavior on phones.
 */
@Composable
fun AnimatedSingleTopStack(
    modifier: Modifier = Modifier,
    state: NavigationState = LocalNavigationState.current,
) {
    // Keep track of running screen transitions
    val transitionTracker = rememberEntryExitTransitionTracker(navigationState = state)

    CompositionLocalProvider(
        LocalNavigationState provides state,
        LocalExitTransitionTracker provides transitionTracker
    ) {
        // Pop destinations off the stack on back press
        PopOnBackHandler()

        Box(modifier = modifier) {
            // Filter for those screens that actually need to be rendered.
            // This filters screens that are not visible at this moment,
            // such as those that currently are deep down in the stack.
            val screensToRender by rememberAnimatedSingleTopStackVisibleScreens()

            val stateHolder: SaveableStateHolder = rememberSaveableStateHolder()

            screensToRender.forEach { (zIndex, screen) ->
                key(screen.composeKey) {
                    // Render the screen at the appropriate zIndex of the stack
                    Box(modifier = Modifier.zIndex(zIndex)) {
                        // provide every screen with its own saved state
                        stateHolder.SaveableStateProvider(screen.savedStateKey) {
                            screen.Content()
                        }
                    }
                }
            }
        }
    }
}