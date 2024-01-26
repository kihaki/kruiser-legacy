package de.gaw.kruiser.backstack.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.MutableBackstack
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.ui.transition.orchestrator.BackstackRenderer
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.LocalSaveableStateHolder
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack

@Composable
fun Backstack(
    modifier: Modifier = Modifier,
    backstack: MutableBackstack = rememberSaveableBackstack(),
    stateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
) {
    WithBackstack(
        backstack = backstack,
        stateHolder = stateHolder,
    ) {
        BackstackRenderer(modifier = modifier)
    }
}

/**
 * Handles back presses and sets the [LocalMutableBackstack] defers rendering to the [content] composable.
 */
@Composable
fun WithBackstack(
    backstack: MutableBackstack = rememberSaveableBackstack(),
    stateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
    content: @Composable (Backstack) -> Unit,
) {
    val entries by backstack.collectEntries()

    BackHandler(
        enabled = entries.size > 1,
        onBack = backstack::pop,
    )

    CompositionLocalProvider(
        LocalMutableBackstack provides backstack,
        LocalBackstack provides backstack,
        LocalSaveableStateHolder provides stateHolder,
    ) {
        content(backstack)
    }
}


