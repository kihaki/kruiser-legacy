package de.gaw.kruiser.transition

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.ui.unit.IntOffset
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.screen.ScreenTransition
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.NavigationState.Event.Idle
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.NavigationState.Event.Push
import de.gaw.kruiser.state.NavigationState.Event.Replace
import de.gaw.kruiser.state.currentLastEvent

fun HorizontalCardStackTransition(
    animSpec: FiniteAnimationSpec<IntOffset> = tween(350),
) = HorizontalCardStackTransition(animSpec, animSpec)

data class HorizontalCardStackTransition(
    val inSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    val outSpec: FiniteAnimationSpec<IntOffset> = tween(350),
) : ScreenTransition {

    @OptIn(ExperimentalAnimationApi::class)
    override fun transition(
        scope: AnimatedContentScope<Destination?>,
        navigationState: NavigationState,
    ): ContentTransform = when (navigationState.currentLastEvent) {
        Idle,
        Push,
        Replace,
        -> slideInHorizontally(inSpec) { it } with slideOutHorizontally(inSpec) { 1 }

        Pop,
        -> slideInHorizontally(outSpec) { 1 } with slideOutHorizontally(outSpec) { it }
    }
}