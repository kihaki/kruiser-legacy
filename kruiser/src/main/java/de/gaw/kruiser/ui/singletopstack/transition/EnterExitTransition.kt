package de.gaw.kruiser.ui.singletopstack.transition

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
    inAnimation: EntryExitTransitionTracker.() -> EnterTransition,
    outAnimation: EntryExitTransitionTracker.() -> ExitTransition,
    scopedServiceProvider: ScopedServiceProvider = LocalScopedServiceProvider.current,
    transitionTracker: EntryExitTransitionTracker = LocalEntryExitTransitionTracker.current,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val screenTransitionState by transitionTracker.collectTransitionState(
        destination = destination,
    )

    val enterTransition = remember(transitionTracker) { inAnimation(transitionTracker) }
    val exitTransition = remember(transitionTracker) { outAnimation(transitionTracker) }

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