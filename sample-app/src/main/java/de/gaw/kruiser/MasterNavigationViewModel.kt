package de.gaw.kruiser

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import de.gaw.kruiser.backstack.savedstate.PersistedMutableBackstack
import de.gaw.kruiser.example.ExamplesListDestination

/**
 * ViewModel that holds the app navigation backstack and persists it via the provided [SavedStateHandle].
 */
class MasterNavigationViewModel(savedState: SavedStateHandle) : ViewModel() {
    val backstack = savedState.PersistedMutableBackstack(
        id = "nav:master",
        initial = listOf(
            ExamplesListDestination,
        ),
    )
}

@Composable
fun activityViewModelStoreOwner() =
    LocalContext.current as ViewModelStoreOwner

/**
 * Provides the [MasterNavigationViewModel] that holds the app navigation.
 */
@Composable
fun masterNavigationStateViewModel(): MasterNavigationViewModel =
    viewModel(
        viewModelStoreOwner = activityViewModelStoreOwner(),
        factory = viewModelFactory {
            addInitializer(MasterNavigationViewModel::class) {
                MasterNavigationViewModel(createSavedStateHandle())
            }
        },
    )