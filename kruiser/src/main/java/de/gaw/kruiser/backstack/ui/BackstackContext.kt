package de.gaw.kruiser.backstack.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.MutableBackstackState
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.util.LocalBackstackState
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstackState
import de.gaw.kruiser.backstack.ui.util.LocalSaveableStateHolder
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.viewmodel.BackstackEntryViewModelStoreOwners
import de.gaw.kruiser.viewmodel.ClearViewModelsForAbandonedEntriesEffect
import de.gaw.kruiser.viewmodel.LocalBackstackEntryViewModelStoreOwners
import de.gaw.kruiser.viewmodel.backstackStateViewModelStoreOwners

/**
 * Handles back presses and sets the [LocalMutableBackstackState] defers rendering to the [content] composable.
 */
@Composable
fun BackstackContext(
    backstackState: MutableBackstackState = LocalMutableBackstackState.currentOrThrow,
    stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current
        ?: rememberSaveableStateHolder(),
    content: @Composable (BackstackState) -> Unit,
) {
    val entries by backstackState.collectEntries()
    val currentEntry = entries.lastOrNull()

    val backstackViewModelStoreOwners = remember {
        backstackStateViewModelStoreOwners.getOrPut(backstackState.id) { BackstackEntryViewModelStoreOwners() }
    }

    CompositionLocalProvider(
        LocalMutableBackstackState provides backstackState,
        LocalBackstackState provides backstackState,
        LocalSaveableStateHolder provides stateHolder,
        LocalBackstackEntry provides currentEntry,
        LocalBackstackEntryViewModelStoreOwners provides backstackViewModelStoreOwners,
    ) {
        ClearViewModelsForAbandonedEntriesEffect(backstackState)

        stateHolder.SaveableStateProvider(key = "bs:${backstackState.id}") {
            BackHandler(
                enabled = entries.size > 1,
                onBack = backstackState::pop,
            )

            content(backstackState)
        }
    }
}