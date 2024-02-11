package de.gaw.kruiser.backstack.ui.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.core.BackstackEntry

@Composable
fun CardTransition(
    modifier: Modifier = Modifier,
    isAnimatedIn: (Backstack, BackstackEntry) -> Boolean = defaultAnimatedTransitionIsAnimatedIn,
    label: String? = null,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedTransition(
        modifier = modifier,
        label = label,
        isVisible = isAnimatedIn,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it },
        content = content,
    )
}
