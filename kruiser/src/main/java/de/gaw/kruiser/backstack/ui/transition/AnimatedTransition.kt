package de.gaw.kruiser.backstack.ui.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.transition.ScreenTransitionState.EnterTransitionRunning
import de.gaw.kruiser.backstack.ui.transition.ScreenTransitionState.EntryTransitionDone
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow

val defaultAnimatedTransitionIsAnimatedIn: (Backstack, BackstackEntry) -> Boolean =
    { stack, entry -> stack.entries.value.contains(entry) }

@Composable
fun AnimatedTransition(
    enter: EnterTransition,
    exit: ExitTransition,
    modifier: Modifier = Modifier,
    isVisible: (Backstack, BackstackEntry) -> Boolean = defaultAnimatedTransitionIsAnimatedIn,
    label: String? = null,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    val entry: BackstackEntry = LocalBackstackEntry.currentOrThrow
    val transitionTracker = LocalScreenTransitionTracker.currentOrThrow

    var visible by remember {
        val isInitiallyVisible = transitionTracker.transitionState(entry)?.let {
            it == EnterTransitionRunning || it == EntryTransitionDone
        } ?: false
        mutableStateOf(isInitiallyVisible)
    }

    val backstack = LocalBackstack.currentOrThrow
    val backstackEntries by backstack.collectEntries()
    LaunchedEffect(backstackEntries, backstack, entry) {
        visible = isVisible(backstack, entry)
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = enter,
        exit = exit,
        label = label ?: remember { "transition for ${entry.id}" }
    ) {
        content()
        UpdateTransitionStateEffect()
    }
}