package de.gaw.kruiser.backstack.ui.transition.orchestrator.transition

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.BackstackEntry
import de.gaw.kruiser.backstack.ui.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.transition.orchestrator.LocalScreenTransitionBackstack
import de.gaw.kruiser.backstack.ui.transition.orchestrator.UpdateTransitionStateEffect
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
    val entry: BackstackEntry = LocalBackstackEntry.current
    val transitionTracker = LocalScreenTransitionBackstack.currentOrThrow

    var visible by rememberSaveable(key = "${entry.id}-visible") {
        Log.v("VisibleThing", "Creating visible for $entry")
        mutableStateOf(transitionTracker.initialVisibility(entry))
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