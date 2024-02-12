package de.gaw.kruiser.backstack.ui.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.util.currentOrThrow

@Composable
fun AnimatedTransition(
    enter: EnterTransition,
    exit: ExitTransition,
    modifier: Modifier = Modifier,
    label: String? = null,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val entry: BackstackEntry = LocalBackstackEntry.currentOrThrow
    val isVisible by rememberIsVisible()

    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = enter,
        exit = exit,
        label = label ?: remember { "transition for ${entry.id}" }
    ) {
        content()
        UpdateTransitionStateEffect()
    }
}

