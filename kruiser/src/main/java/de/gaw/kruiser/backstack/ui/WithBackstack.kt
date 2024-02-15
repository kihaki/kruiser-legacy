package de.gaw.kruiser.backstack.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.core.MutableBackstack
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.results.BackstackResultsStore
import de.gaw.kruiser.backstack.results.LocalBackstackEntriesResultsStore
import de.gaw.kruiser.backstack.results.rememberSaveableBackstackResultsStore
import de.gaw.kruiser.backstack.ui.rendering.BackstackRenderer
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.LocalSaveableStateHolder
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack

@Composable
fun Backstack(
    modifier: Modifier = Modifier,
    backstack: MutableBackstack = rememberSaveableBackstack(),
    stateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
    backstackResultsStore: BackstackResultsStore = LocalBackstackEntriesResultsStore.current ?: rememberSaveableBackstackResultsStore(),
) {
    BackstackContext(
        mutableBackstack = backstack,
        stateHolder = stateHolder,
        backstackResultsStore = backstackResultsStore,
    ) {
        BackstackRenderer(
            modifier = modifier,
            backstack = backstack,
        )
    }
}

/**
 * Handles back presses and sets the [LocalMutableBackstack] defers rendering to the [content] composable.
 */
@Composable
fun BackstackContext(
    mutableBackstack: MutableBackstack = rememberSaveableBackstack(),
    backstack: Backstack = mutableBackstack,
    stateHolder: SaveableStateHolder = rememberSaveableStateHolder(),
    backstackResultsStore: BackstackResultsStore = LocalBackstackEntriesResultsStore.current ?: rememberSaveableBackstackResultsStore(),
    content: @Composable (Backstack) -> Unit,
) {
    CompositionLocalProvider(
        LocalMutableBackstack provides mutableBackstack,
        LocalBackstack provides backstack,
        LocalSaveableStateHolder provides stateHolder,
        LocalBackstackEntriesResultsStore provides backstackResultsStore,
    ) {
        stateHolder.SaveableStateProvider(key = "bs:${backstack.id}") {
            val entries by mutableBackstack.collectEntries()

            BackHandler(
                enabled = entries.size > 1,
                onBack = mutableBackstack::pop,
            )

            content(backstack)
        }
    }
}


