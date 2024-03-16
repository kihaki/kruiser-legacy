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
import de.gaw.kruiser.viewmodel.BackstackViewModelStoreOwners
import de.gaw.kruiser.viewmodel.ClearViewModelsForAbandonedEntriesEffect
import de.gaw.kruiser.viewmodel.LocalBackstackEntryViewModelStoreOwners
import de.gaw.kruiser.viewmodel.backstackStateViewModelStoreOwners

/**
 * Handles back presses and sets the [LocalMutableBackstackState] defers rendering to the [content] composable.
 */
@Composable
fun Backstack(
    state: MutableBackstackState,
    saveableStateHolder: SaveableStateHolder = LocalSaveableStateHolder.current ?: rememberSaveableStateHolder(),
    content: @Composable (BackstackState) -> Unit,
) {
    val entries by state.collectEntries()
    val currentEntry = entries.lastOrNull()

    val backstackViewModelStoreOwners = remember(state.id) {
        backstackStateViewModelStoreOwners.getOrPut(state.id) { BackstackViewModelStoreOwners() }
    }

    CompositionLocalProvider(
        LocalMutableBackstackState provides state,
        LocalBackstackState provides state,
        LocalSaveableStateHolder provides saveableStateHolder,
        LocalBackstackEntry provides currentEntry,
        LocalBackstackEntryViewModelStoreOwners provides backstackViewModelStoreOwners,
    ) {
        ClearViewModelsForAbandonedEntriesEffect(state)

        saveableStateHolder.SaveableStateProvider(key = "bs:${state.id}") {
            BackHandler(
                enabled = entries.size > 1,
                onBack = state::pop,
            )

            content(state)
        }
    }
}