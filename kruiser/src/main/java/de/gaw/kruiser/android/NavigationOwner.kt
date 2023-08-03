package de.gaw.kruiser.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gaw.kruiser.service.DefaultScopedServiceProvider
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.SavedStateNavigationState

@Composable
fun navigationOwnerViewModel() = viewModel<NavigationOwnerViewModel>(
    viewModelStoreOwner = LocalContext.current as? ViewModelStoreOwner
        ?: error("Current context is not a viewModelStoreOwner."),
    factory = NavigatorOwnerViewModelFactory(),
)

private class NavigatorOwnerViewModelFactory : AbstractSavedStateViewModelFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ): T = NavigationOwnerViewModel(handle) as T
}

@Composable
fun defaultNavigationState(): NavigationState = navigationOwnerViewModel().state

val LocalNavigationState = staticCompositionLocalOf<NavigationState> {
    error("No NavigationState provided.")
}

@Composable
fun defaultServiceProvider(): ScopedServiceProvider = navigationOwnerViewModel().serviceProvider

val LocalScopedServiceProvider = staticCompositionLocalOf<ScopedServiceProvider> {
    error("No ScopedServiceProvider provided.")
}

interface NavigationOwner {
    val state: NavigationState
    val serviceProvider: ScopedServiceProvider
}

class NavigationOwnerViewModel(
    savedStateHandle: SavedStateHandle,
) : NavigationOwner, ViewModel() {
    override val state = SavedStateNavigationState(
        navStateKey = "navigation_state",
        eventStateKey = "navigation_event",
        savedStateHandle = savedStateHandle,
    )
    override val serviceProvider = DefaultScopedServiceProvider(state)
}