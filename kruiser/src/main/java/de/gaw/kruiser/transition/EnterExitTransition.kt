package de.gaw.kruiser.transition

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.ClearDeadServicesDisposableEffect
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.collectCurrentEvent
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter

/**
 * Entry/Exit Animation that can be used inside of a [Screen] to provide animations.
 */
@Composable
fun Screen.EnterExitTransition(
    inAnimation: NavigationState.() -> EnterTransition,
    outAnimation: NavigationState.() -> ExitTransition,
    navigationState: NavigationState = LocalNavigationState.current,
    exitAnimationsState: ExitAnimationsState = LocalExitAnimationsState.current,
    scopedServiceProvider: ScopedServiceProvider = LocalScopedServiceProvider.current,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val screenTransitionState = rememberScreenTransitionState(
        navigationState = navigationState,
        exitAnimationsState = exitAnimationsState,
    )

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

@Composable
fun Screen.rememberScreenTransitionState(
    navigationState: NavigationState,
    exitAnimationsState: ExitAnimationsState,
): MutableTransitionState<Boolean> {
    // Tell the ExitAnimationsState that this Destination will run exit animations.
    RegisterExitAnimationEffect(exitAnimationsState = exitAnimationsState)

    // Create a MutableTransitionState for this Destination's enter/exit animation
    val destinationTransitionState = rememberDestinationTransitionState(navigationState = navigationState)

    // React to the ExitAnimationsState and set the correct transitionState target (visible/invisible)
    SetScreenTransitionTargetFromExitAnimationsState(
        transitionState = destinationTransitionState,
        exitAnimationsState = exitAnimationsState,
    )

    // After animating out, remove this destination from the ExitAnimationsState cache
    RemoveDestinationAfterExitAnimationIsDoneEffect(
        transitionState = destinationTransitionState,
        exitAnimationsState = exitAnimationsState,
    )

    return destinationTransitionState
}

/**
 * Registers this [Screen]s [Destination] in the [ExitAnimationsState].
 * This tells the [ExitAnimationsState] to cache this [Destination] after it has been removed
 * from the stack so it can run its exit animations before being disposed.
 */
@Composable
private fun Screen.RegisterExitAnimationEffect(
    exitAnimationsState: ExitAnimationsState,
) {
    DisposableEffect(Unit) {
        exitAnimationsState.registerExitAnimationSupport(destination)
        onDispose {
            exitAnimationsState.unregisterExitAnimationSupport(destination)
        }
    }
}

/**
 * Registers this [Screen]s [Destination] in the [ExitAnimationsState].
 * This tells the [ExitAnimationsState] to cache this [Destination] after it has been removed
 * from the stack so it can run its exit animations before being disposed.
 */
@Composable
private fun rememberDestinationTransitionState(
    navigationState: NavigationState,
): MutableTransitionState<Boolean> {
    val event by navigationState.collectCurrentEvent()
    return remember {
        MutableTransitionState(
            initialState = when (event) {
                Pop -> true
                else -> false
            }
        ).apply {
            targetState = true
        }
    }
}

/**
 * Sets the correct target for [transitionState] when the [Destination] should run its exit
 * animations before being removed.
 * Removes the [Destination] from the [ExitAnimationsState]'s list of exiting [Destination]s
 * once the exit animation is completed.
 */
@Composable
private fun Screen.SetScreenTransitionTargetFromExitAnimationsState(
    transitionState: MutableTransitionState<Boolean>,
    exitAnimationsState: ExitAnimationsState,
) {
    val exitingDestinations by exitAnimationsState.destinationsRunningExitAnimations.collectAsState()
    val destinationExits by remember {
        derivedStateOf { exitingDestinations.contains(destination) }
    }
    LaunchedEffect(destinationExits) {
        val destinationIsVisible = !destinationExits
        transitionState.targetState = destinationIsVisible
        Log.v("AnimationThing", "Animating Out $destination")
    }
}

/**
 * Removes the [Destination] from the [ExitAnimationsState]'s list of exiting [Destination]s
 * once the exit animation is completed.
 */
@Composable
private fun Screen.RemoveDestinationAfterExitAnimationIsDoneEffect(
    transitionState: MutableTransitionState<Boolean>,
    exitAnimationsState: ExitAnimationsState,
) {
    LaunchedEffect(Unit) {
        combine(
            snapshotFlow { transitionState.targetState },
            snapshotFlow { transitionState.isIdle }
        ) { targetState, isIdle -> targetState to isIdle }
            .filter { (isVisible, isIdle) -> !isVisible && isIdle }
            .collect { (_, _) ->
                exitAnimationsState.onExitAnimationCompleted(destination)
                Log.v("AnimationThing", "Done Animating Out $destination")
            }
    }
}
