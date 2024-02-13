package de.gaw.kruiser.backstack.ui.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.util.rememberIsInBack

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
    // Background blurring when not focused experiment
    val isBlurred by rememberIsInBack()
    val isBackgroundBlur by animateDpAsState(
        if (isBlurred) 16.dp else 0.dp,
        label = "background-blur"
    )

    AnimatedTransition(
        modifier = Modifier
            .blur(isBackgroundBlur)
            .then(modifier),
        label = label,
        enter = slideInHorizontally(animationSpec) { it },
        exit = slideOutHorizontally(animationSpec) { it },
        content = content,
    )
}
