package de.gaw.kruiser.backstack.ui.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

@Composable
fun BottomCardTransition(
    modifier: Modifier = Modifier,
//    animationSpec: FiniteAnimationSpec<IntOffset> = spring(
//        stiffness = StiffnessMediumLow,
//        visibilityThreshold = IntOffset.VisibilityThreshold,
//    ),
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(delayMillis = 16),
    label: String? = null,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    // TODO: Reuse blurring and other effects later when implementing them properly
    // Background blurring when not focused experiment
//    val isBlurred by rememberIsInBack()
//    val isBackgroundBlur by animateDpAsState(
//        if (isBlurred) 16.dp else 0.dp,
//        label = "background-blur"
//    )

    AnimatedTransition(
//        modifier = Modifier
//            .blur(isBackgroundBlur)
//            .then(modifier),
        modifier = modifier,
        label = label,
        enter = slideInVertically(animationSpec) { it },
        exit = slideOutVertically(animationSpec) { it },
        content = content,
    )
}
