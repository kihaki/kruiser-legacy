package de.gaw.kruiser.ui.doublerailstack

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.ui.PopOnBackHandler
import de.gaw.kruiser.ui.singletopstack.AnimatedSingleTopStackScreen
import de.gaw.kruiser.ui.singletopstack.transition.EntryExitTransitionTracker
import de.gaw.kruiser.ui.singletopstack.transition.LocalEntryExitTransitionTracker
import de.gaw.kruiser.ui.singletopstack.transition.collectCurrentEntryTransition
import de.gaw.kruiser.ui.singletopstack.transition.collectCurrentExitTransition
import de.gaw.kruiser.ui.singletopstack.transition.rememberEntryExitTransitionTracker

/**
 * A composable to render always the most recent two screens side by side.
 * aka some fancy behavior.
 */
@Composable
fun AnimatedDoubleRailStack(
    modifier: Modifier = Modifier,
    state: NavigationState = LocalNavigationState.current,
) {
    // Keep track of running screen transitions
    val transitionTracker = rememberEntryExitTransitionTracker(navigationState = state)

    CompositionLocalProvider(
        LocalNavigationState provides state,
        LocalEntryExitTransitionTracker provides transitionTracker
    ) {
        // Pop destinations off the stack on back press
        PopOnBackHandler()

        Row(modifier = modifier) {
            // Filter for those screens that actually need to be rendered.
            // This filters screens that are not visible at this moment,
            // such as those that currently are deep down in the stack.
            val screensToRender by rememberDoubleRailStackVisibleScreens()

            val stateHolder: SaveableStateHolder = rememberSaveableStateHolder()

            val first by remember {
                derivedStateOf {
                    when {
                        screensToRender.size <= 1 -> screensToRender.lastOrNull()
                        else -> screensToRender.dropLast(1).lastOrNull()
                    }
                }
            }
            val second by remember {
                derivedStateOf {
                    when {
                        screensToRender.size <= 1 -> null
                        else -> screensToRender.lastOrNull()
                    }
                }
            }

            // Slot first
            Box(
                modifier = Modifier
                    .zIndex(first?.zIndex ?: 1f)
                    .weight(1f),
            ) {
                first?.let { (_, screen) ->
                    key(screen.composeKey) {
                        // provide every screen with its own saved state
                        stateHolder.SaveableStateProvider(screen.savedStateKey) {
                            screen.Content()
                        }
                    }
                }
            }

            // Slot second
            Box(
                modifier = Modifier
                    .zIndex(second?.zIndex ?: 1f)
                    .weight(1f),
            ) {
                second?.let { (_, screen) ->
                    key(screen.composeKey) {
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

@Composable
fun rememberDoubleRailStackVisibleScreens(
    state: NavigationState = LocalNavigationState.current,
    transitionTracker: EntryExitTransitionTracker = LocalEntryExitTransitionTracker.current,
): State<List<AnimatedSingleTopStackScreen>> {
    // Collect the current navigation stack
    val stack by state.collectCurrentStack()

    // Keep track of screens that are removed from the stack but still need to be animated out
    val exitTransition by transitionTracker.collectCurrentExitTransition()
    // We also need to know if we are doing entry animations atm
    val entryTransition by transitionTracker.collectCurrentEntryTransition()
    val isAnimating by remember { derivedStateOf { entryTransition != null || exitTransition != null } }

    // All screens on stack with additionally the exiting destination if it exists
    return remember {
        derivedStateOf {
            val screens = (stack + exitTransition?.destination)
                .filterNotNull()
                .buildScreens()
                .takeVisibleScreens(isAnimating)
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
        true -> 3 // Keep top three screens if animating
        false -> 2 // Keep the top two screens only if the animations are not running
    }
    return takeLast(visibleScreensCount)
}