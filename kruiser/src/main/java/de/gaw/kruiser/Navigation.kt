package de.gaw.kruiser

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.Start
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.NavigationState.Event.Idle
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.NavigationState.Event.Push
import de.gaw.kruiser.state.NavigationState.Event.Replace
import de.gaw.kruiser.state.currentLastEvent
import de.gaw.kruiser.state.currentStack
import de.gaw.kruiser.state.isEmpty
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.state.rememberCurrentDestination
import de.gaw.kruiser.state.rememberIsEmpty

@Composable
fun Navigation(
    state: NavigationState,
    serviceProvider: ScopedServiceProvider,
    modifier: Modifier = Modifier,
) {
    val isEmpty by state.rememberIsEmpty()

    BackHandler(
        enabled = isEmpty,
        onBack = state::pop,
    )

    SlideOverTransition(
        modifier = modifier,
        state = state,
        serviceProvider = serviceProvider,
        animationSpec = remember { tween(durationMillis = 350) },
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SlideOverTransition(
    state: NavigationState,
    serviceProvider: ScopedServiceProvider,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<IntOffset> = remember { tween(durationMillis = 350) },
) {
    val currentDestination by state.rememberCurrentDestination()
    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        targetState = currentDestination,
        transitionSpec = {
            when (state.lastEvent.value) {
                Idle,
                Push,
                Replace,
                -> slideIntoContainer(
                    towards = Start,
                    animationSpec = animationSpec,
                ) { it } with slideOutOfContainer(
                    towards = Start,
                    animationSpec = animationSpec,
                ) { 1 }

                Pop,
                -> slideIntoContainer(
                    towards = End,
                    animationSpec = animationSpec,
                ) { 1 } with slideOutOfContainer(
                    towards = End,
                    animationSpec = animationSpec,
                ) { it }
            }.apply {
                val size = state.currentStack.size
                targetContentZIndex = when (state.currentLastEvent) {
                    Idle,
                    Push,
                    Replace,
                    -> size

                    Pop,
                    -> size - 1
                }.toFloat()
            }
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