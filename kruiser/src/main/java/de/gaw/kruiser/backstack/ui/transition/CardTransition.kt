package de.gaw.kruiser.backstack.ui.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

@Composable
fun CardTransition(
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
        stiffness = StiffnessMediumLow,
        visibilityThreshold = IntOffset.VisibilityThreshold,
    ),
    label: String? = null,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedTransition(
        modifier = modifier,
        label = label,
        enter = slideInHorizontally(animationSpec) { it },
        exit = slideOutHorizontally(animationSpec) { it },
        content = content,
    )
}
