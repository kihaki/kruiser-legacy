package de.gaw.kruiser.backstack.ui.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.Backstack

/**
 * Renders screens as if they were a stack of cards.
 */
@Composable
fun CardstackAnimation(
    backstack: Backstack,
    modifier: Modifier = Modifier,
) = ScreenAnimation(
    backstack = backstack,
    modifier = modifier,
    animationSpec = cardStackAnimationSpec,
)

