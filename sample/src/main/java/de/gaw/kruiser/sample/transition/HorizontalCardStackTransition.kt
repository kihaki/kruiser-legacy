package de.gaw.kruiser.sample.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.transition.EnterExitTransition

/**
 * Transition where the Screen animates in from the right and out towards the right,
 * like adding cards on a card stack.
 */
@Composable
fun Screen.HorizontalCardStackTransition(
    inSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    outSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    isEnabled: Boolean = true,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) = EnterExitTransition(
    inAnimation = {
        when {
            isEnabled -> slideInHorizontally(inSpec) { size: Int -> size }
            else -> EnterTransition.None
        }
    },
    outAnimation = {
        when {
            isEnabled -> slideOutHorizontally(outSpec) { size: Int -> size }
            else -> ExitTransition.None
        }
    },
    content = content,
)