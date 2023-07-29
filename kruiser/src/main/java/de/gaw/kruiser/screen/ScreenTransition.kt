package de.gaw.kruiser.screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState

@OptIn(ExperimentalAnimationApi::class)
fun interface ScreenTransition {
    fun transition(
        scope: AnimatedContentScope<Destination?>,
        navigationState: NavigationState,
    ): ContentTransform
}