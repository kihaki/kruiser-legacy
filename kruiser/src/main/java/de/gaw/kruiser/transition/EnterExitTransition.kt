package de.gaw.kruiser.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.ClearDeadServicesDisposableEffect
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack

/**
 * Entry/Exit Animation that can be used inside of a [Screen] to provide animations.
 */
@Composable
fun Screen.EnterExitTransition(
    inAnimation: ExitTransitionTracker.() -> EnterTransition,
    outAnimation: ExitTransitionTracker.() -> ExitTransition,
    navigationState: NavigationState = LocalNavigationState.current,
    exitTransitionTracker: ExitTransitionTracker = LocalExitTransitionTracker.current,
    scopedServiceProvider: ScopedServiceProvider = LocalScopedServiceProvider.current,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val stack by navigationState.collectCurrentStack()
    val isFirstOnStack by remember { derivedStateOf { stack.size == 1 && stack.firstOrNull() == destination } }
    val initialState = remember {
        MutableTransitionState(initialState = isFirstOnStack)
            .apply { targetState = true }
    }
    val screenTransitionState by exitTransitionTracker.collectTransitionState(
        initialState = initialState,
        destination = destination,
    )

    val enterTransition = remember(exitTransitionTracker) { inAnimation(exitTransitionTracker) }
    val exitTransition = remember(exitTransitionTracker) { outAnimation(exitTransitionTracker) }

    AnimatedVisibility(
        visibleState = screenTransitionState,
        enter = enterTransition,
        exit = exitTransition,
        content = {
            ClearDeadServicesDisposableEffect(scopedServiceProvider = scopedServiceProvider)
            content()
        },
    )
}