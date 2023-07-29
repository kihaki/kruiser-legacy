package de.gaw.kruiser

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.gaw.kruiser.screen.ScreenTransition
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.NavigationState.Event.Idle
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.NavigationState.Event.Push
import de.gaw.kruiser.state.NavigationState.Event.Replace
import de.gaw.kruiser.state.currentLastEvent
import de.gaw.kruiser.state.currentStack
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.state.rememberCurrentDestination
import de.gaw.kruiser.state.rememberIsEmpty
import de.gaw.kruiser.transition.HorizontalCardStackTransition

@Composable
fun Navigation(
    state: NavigationState,
    serviceProvider: ScopedServiceProvider,
    modifier: Modifier = Modifier,
) {
    val isEmpty by state.rememberIsEmpty()

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
    val currentDestination by state.rememberCurrentDestination()
    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
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