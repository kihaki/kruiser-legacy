package de.gaw.kruiser.backstack.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.MutableBackstackState
import de.gaw.kruiser.backstack.pop
import de.gaw.kruiser.backstack.ui.rendering.LocalBackstackEntry
import de.gaw.kruiser.backstack.ui.util.LocalBackstackState
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstackState
import de.gaw.kruiser.backstack.ui.util.LocalSaveableStateHolder
import de.gaw.kruiser.backstack.ui.util.collectEntries
import de.gaw.kruiser.viewmodel.BackstackViewModelStoreOwners
import de.gaw.kruiser.viewmodel.DisposeViewModelsEffect
import de.gaw.kruiser.viewmodel.LocalBackstackEntryViewModelStoreOwners
import de.gaw.kruiser.viewmodel.backstackStateViewModelStoreOwners

/**
 * Sets the conditions for rendering a [MutableBackstackState], such as:
 * 1. Creating and setting [ViewModelStoreOwner]s so that [ViewModel]s and their disposal works for
 * [BackstackEntry]s.
 * 1. Sets [CompositionLocalProvider]s like [LocalBackstackState] and [LocalBackstackEntry].
 * 1. Creates and manages the [SaveableStateHolder] for the [BackstackEntry]s.
 * 1. Handles back press events for the backstack (configurable via [handleBackPress], enabled by
 * default).
 */
@Composable
fun BackstackContext(
    backstackState: MutableBackstackState,
    saveableStateHolder: SaveableStateHolder = LocalSaveableStateHolder.current ?: rememberSaveableStateHolder(),
    handleBackPress: Boolean = true,
    content: @Composable (BackstackState) -> Unit,
) {
    /* Most up to date backstack entries. */
    val entries by backstackState.collectEntries()
    /* Backstack entry currently at the top. */
    val currentEntry = entries.lastOrNull()

    /* Configures the ViewModelStoreOwners for the backstack so that ViewModels just work. */
    val backstackViewModelStoreOwners = remember(backstackState.id) {
        backstackStateViewModelStoreOwners.getOrPut(backstackState.id) { BackstackViewModelStoreOwners() }
    }

    CompositionLocalProvider(
        LocalMutableBackstackState provides backstackState,
        LocalBackstackState provides backstackState,
        LocalSaveableStateHolder provides saveableStateHolder,
        LocalBackstackEntry provides currentEntry,
        LocalBackstackEntryViewModelStoreOwners provides backstackViewModelStoreOwners,
    ) {
        /* Disposes ViewModels for BackstackEntries at the appropriate time. */
        DisposeViewModelsEffect(backstackState)

        saveableStateHolder.SaveableStateProvider(key = "bs:${backstackState.id}") {

            BackHandler(
                enabled = handleBackPress && entries.size > 1,
                onBack = backstackState::pop,
            )

            content(backstackState)
        }
    }
}