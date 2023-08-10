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
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.collectIsEmpty
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.transition.LocalExitTransitionTracker
import de.gaw.kruiser.transition.collectCurrentExitTransition
import de.gaw.kruiser.transition.rememberExitTransitionTracker

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
                DestinationContainer(
                    destination = destination,
                    zIndex = index.toFloat(),
                    content = { screen -> screen.Content() },
                )
            }
            exitTransition?.let { (destination, _) ->
                DestinationContainer(
                    destination = destination,
                    zIndex = stack.size.toFloat(),
                    content = { screen -> screen.Content() },
                )
            }
            remoteUiComponents()
        }
    }
}

@Composable
private inline fun DestinationContainer(
    destination: Destination,
    zIndex: Float,
    modifier: Modifier = Modifier,
    content: @Composable (screen: Screen) -> Unit,
) {
    Box(
        modifier = Modifier
            .zIndex(zIndex)
            .then(modifier),
    ) {
        key(destination) {
            val screen = remember(destination) { destination.build() }
            content(screen)
        }
    }
}