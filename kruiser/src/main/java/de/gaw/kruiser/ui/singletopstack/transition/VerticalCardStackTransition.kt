package de.gaw.kruiser.ui.singletopstack.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import de.gaw.kruiser.ui.singletopstack.ScreenStackRenderContext

/**
 * Transition where the Screen animates in from the bottom and out towards the bottom,
 * like adding cards on a card stack from below.
 */
@Composable
fun ScreenStackRenderContext.VerticalCardStackTransition(
    inSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    outSpec: FiniteAnimationSpec<IntOffset> = tween(350),
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    visibleState = transitionState,
    enter = slideInVertically(inSpec) { size: Int -> size },
    exit = slideOutVertically(outSpec) { size: Int -> size },
    content = content,
)