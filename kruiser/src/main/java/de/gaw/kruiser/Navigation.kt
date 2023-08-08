package de.gaw.kruiser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.collectIsEmpty
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.transition.LocalExitTransitionTracker
import de.gaw.kruiser.transition.collectCurrentExitTransition
import de.gaw.kruiser.transition.rememberExitTransitionTracker

@Composable
fun Destination.Render() {
    key(this) {
        val screen = remember(this) { build() }
        screen.Content()
    }
}

@Composable
fun AnimatedNavigation(
    modifier: Modifier = Modifier,
    state: NavigationState = LocalNavigationState.current,
    remoteUiComponents: @Composable () -> Unit = {},
) {
    val isEmpty by state.collectIsEmpty()

    BackHandler(
        enabled = !isEmpty,
        onBack = state::pop,
    )

    val stack by state.collectCurrentStack()
    val exitTransitionTracker = rememberExitTransitionTracker(navigationState = state)
    val exitTransition by exitTransitionTracker.collectCurrentExitTransition()

    Box(modifier = modifier) {
        CompositionLocalProvider(
            LocalExitTransitionTracker provides exitTransitionTracker
        ) {
            stack.forEachIndexed { index, destination ->
                Box(modifier = Modifier.zIndex(index.toFloat())) {
                    destination.Render()
                }
            }
            exitTransition?.let { (destination, _) ->
                Box(modifier = Modifier.zIndex(stack.size.toFloat())) {
                    destination.Render()
                }
            }
            remoteUiComponents()
        }
    }
}