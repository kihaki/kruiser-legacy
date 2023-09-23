package de.gaw.kruiser.ui.singletopstack.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import de.gaw.kruiser.ui.singletopstack.ScreenStackRenderContext

/**
 * Transition where the Screen animates in from the right and out towards the right,
 * like adding cards on a card stack from the right.
 */
@Composable
fun ScreenStackRenderContext.HorizontalCardStackTransition(
    inSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    outSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    visibleState = transitionState,
    enter = slideInHorizontally(inSpec) { size: Int -> size },
    exit = slideOutHorizontally(outSpec) { size: Int -> size },
    content = content,
)