package de.gaw.kruiser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.persistent.PersistentUi
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
) {
    val isEmpty by state.collectIsEmpty()

    BackHandler(
        enabled = !isEmpty,
        onBack = state::pop,
    )

    val animatedNavigationState = rememberExitTransitionTracker(navigationState = state)
    val stack by state.collectCurrentStack()
    val exitAnimationTransition by animatedNavigationState.collectCurrentExitTransition()

    Box(modifier = modifier) {
        CompositionLocalProvider(
            LocalExitTransitionTracker provides animatedNavigationState
        ) {
            stack.forEach { destination ->
                destination.Render()
            }
            exitAnimationTransition?.let { (destination, _) ->
                destination.Render()
            }
            PersistentUi()
        }
    }
}