package de.gaw.kruiser.sample.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import de.gaw.kruiser.transition.EnterExitTransition
import de.gaw.kruiser.screen.Screen

@Composable
fun Screen.VerticalCardStackTransition(
    inSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    outSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) = EnterExitTransition(
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