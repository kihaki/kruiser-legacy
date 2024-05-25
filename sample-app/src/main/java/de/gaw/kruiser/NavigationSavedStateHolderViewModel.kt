package de.gaw.kruiser

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import de.gaw.kruiser.backstack.savedstate.SavedStateMutableBackstack
import de.gaw.kruiser.example.ExamplesListDestination

/**
 * ViewModel that holds the app navigation backstack and persists it via the provided [SavedStateHandle].
 */
class NavigationSavedStateHolderViewModel(savedState: SavedStateHandle) : ViewModel() {
    val backstack = SavedStateMutableBackstack(
        handle = savedState,
        id = "nav:master",
        initial = listOf(ExamplesListDestination),
    )
}

@Composable
fun activityViewModelStoreOwner() =
    LocalContext.current as ViewModelStoreOwner

/**
 * Provides the [NavigationSavedStateHolderViewModel] that holds the [Activity]s navigation.
 */
@Composable
fun navigationSavedStateHolderViewModel(): NavigationSavedStateHolderViewModel =
    viewModel<NavigationSavedStateHolderViewModel>(
        viewModelStoreOwner = activityViewModelStoreOwner(),
        factory = viewModelFactory {
            addInitializer(NavigationSavedStateHolderViewModel::class) {
                NavigationSavedStateHolderViewModel(createSavedStateHandle())
            }
        },
    )