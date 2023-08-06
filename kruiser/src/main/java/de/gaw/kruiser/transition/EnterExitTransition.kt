package de.gaw.kruiser.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.ClearDeadServicesDisposableEffect
import de.gaw.kruiser.service.ScopedServiceProvider

/**
 * Entry/Exit Animation that can be used inside of a [Screen] to provide animations.
 */
@Composable
fun Screen.EnterExitTransition(
    inAnimation: AnimatedNavigationState.() -> EnterTransition,
    outAnimation: AnimatedNavigationState.() -> ExitTransition,
    navigationState: AnimatedNavigationState = LocalAnimatedNavigationState.current,
    scopedServiceProvider: ScopedServiceProvider = LocalScopedServiceProvider.current,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val screenTransitionState by navigationState.collectTransitionState(destination = destination)

    val enterTransition = remember(navigationState) { inAnimation(navigationState) }
    val exitTransition = remember(navigationState) { outAnimation(navigationState) }

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