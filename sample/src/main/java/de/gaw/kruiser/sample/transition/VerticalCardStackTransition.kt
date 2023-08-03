package de.gaw.kruiser.sample.transition

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.EnterTransition.Companion.None
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.renderstate.EntryExitTransition
import de.gaw.kruiser.renderstate.LocalDestinationRenderState
import de.gaw.kruiser.renderstate.collectVisibleDestinations
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.screen.ScreenTransition
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.NavigationState.Event.Idle
import de.gaw.kruiser.state.NavigationState.Event.Pop
import de.gaw.kruiser.state.NavigationState.Event.Push
import de.gaw.kruiser.state.NavigationState.Event.Replace
import de.gaw.kruiser.state.collectCurrentEvent
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.currentLastEvent

data class VerticalCardStackTransition(
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
        -> slideInVertically(inSpec) { size: Int -> size } with slideOutVertically(inSpec) { -1 }

        Pop,
        -> None with slideOutVertically(outSpec) { size: Int -> size }
    }
}

@Composable
fun Screen.VerticalCardStackTransition(
    inSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    outSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) = EntryExitTransition(
    inAnimation = {
        slideInVertically(inSpec) { size: Int -> size }
//        when (currentLastEvent) {
//            Idle,
//            Push,
//            Replace,
//            -> slideInVertically(inSpec) { size: Int -> size }
//
//            Pop,
//            -> None
//        }
    },
    outAnimation = {
        slideOutVertically(outSpec) { size: Int -> size }
//        when (currentLastEvent) {
//            Idle,
//            Push,
//            Replace,
//            -> slideOutVertically(inSpec) { -1 }
//
//            Pop,
//            -> slideOutVertically(outSpec) { size: Int -> size }
//        }
    },
    content = content,
)