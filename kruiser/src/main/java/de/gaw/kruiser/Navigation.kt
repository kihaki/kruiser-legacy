package de.gaw.kruiser

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.renderstate.LocalDestinationRenderState
import de.gaw.kruiser.renderstate.collectDestinationsToRenderInOrder
import de.gaw.kruiser.renderstate.rememberDestinationRenderState
import de.gaw.kruiser.screen.ScreenTransition
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.NavigationState.Event.Idle
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.NavigationState.Event.Push
import de.gaw.kruiser.state.NavigationState.Event.Replace
import de.gaw.kruiser.state.collectCurrentDestination
import de.gaw.kruiser.state.collectIsEmpty
import de.gaw.kruiser.state.currentLastEvent
import de.gaw.kruiser.state.currentStack
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.transition.HorizontalCardStackTransition

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    state: NavigationState = LocalNavigationState.current,
) {
    val isEmpty by state.collectIsEmpty()

    BackHandler(
        enabled = !isEmpty,
        onBack = state::pop,
    )

    val renderState = rememberDestinationRenderState(navigationState = state)

    Box(modifier = modifier) {
        CompositionLocalProvider(
            LocalDestinationRenderState provides renderState,
        ) {
            val visibleDestinations by renderState.collectDestinationsToRenderInOrder()
            visibleDestinations.forEachIndexed { index, destination ->
                key(destination) {
                    SideEffect {
                        Log.v("AnimationThing", "$index Rendering $destination")
                    }
                    val screen = remember(destination) { destination.build() }
                    screen.Content()
                }
            }
            SideEffect {
                Log.v("AnimationThing", "===")
            }
        }
    }
}

@Composable
fun NavigationExternalTransitions(
    state: NavigationState,
    serviceProvider: ScopedServiceProvider,
    modifier: Modifier = Modifier,
) {
    val isEmpty by state.collectIsEmpty()

    BackHandler(
        enabled = !isEmpty,
        onBack = state::pop,
    )

    ScreenTransition(
        modifier = modifier,
        state = state,
        serviceProvider = serviceProvider,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ScreenTransition(
    state: NavigationState,
    serviceProvider: ScopedServiceProvider,
    modifier: Modifier = Modifier,
    defaultTransition: ScreenTransition = remember { HorizontalCardStackTransition() },
) {
    val currentDestination by state.collectCurrentDestination()
    AnimatedContent(
        modifier = modifier,
        targetState = currentDestination,
        transitionSpec = {
            val transitionSource = when (state.currentLastEvent) {
                Pop,
                Replace,
                -> initialState

                Idle,
                Push,
                -> targetState
            }

            val transition = (transitionSource as? ScreenTransition)
                ?.transition(this, state)
                ?: defaultTransition.transition(this, state)

            val stackSize = state.currentStack.size
            transition.targetContentZIndex = when (state.currentLastEvent) {
                Idle,
                Push,
                Replace,
                -> stackSize

                Pop,
                -> stackSize - 1
            }.toFloat()

            transition
        },
        label = "navigation-slide-transition",
    ) { destination ->
        DisposableEffect(destination) {
            onDispose {
                // When this composable leaves the composition (= out-animation is done),
                // check if any services need to die
                serviceProvider.clearDeadServices()
            }
        }
        val screen = remember(destination) { destination?.build() }
        screen?.Content()
    }
}