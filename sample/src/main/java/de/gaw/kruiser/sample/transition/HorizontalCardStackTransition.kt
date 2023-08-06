package de.gaw.kruiser.sample.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import de.gaw.kruiser.transition.EntryExitTransition
import de.gaw.kruiser.screen.Screen

@Composable
fun Screen.HorizontalCardStackTransition(
    inSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    outSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) = EntryExitTransition(
    inAnimation = {
        slideInHorizontally(inSpec) { size: Int -> size }
//        when (currentLastEvent) {
//            Idle,
//            Push,
//            Replace,
//            -> slideInHorizontally(inSpec) { size: Int -> size }
//
//            Pop,
//            -> None
//        }
    },
    outAnimation = {
        slideOutHorizontally(outSpec) { size: Int -> size }
//        when (currentLastEvent) {
//            Idle,
//            Push,
//            Replace,
//            -> slideOutHorizontally(inSpec) { -1 }
//
//            Pop,
//            -> slideOutHorizontally(outSpec) { size: Int -> size }
//        }
    },
    content = content,
)