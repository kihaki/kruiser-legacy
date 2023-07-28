package de.gaw.kruiser.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gaw.kruiser.service.DefaultScopedServiceProvider
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.state.DefaultNavigationState
import de.gaw.kruiser.state.NavigationState

@Composable
fun navigationOwnerViewModel() = viewModel<NavigationOwnerViewModel>(
    viewModelStoreOwner = LocalContext.current as? ViewModelStoreOwner
        ?: error("Current context is not a viewModelStoreOwner."),
)

@Composable
fun defaultNavigationState() = navigationOwnerViewModel().state

@Composable
fun defaultServiceProvider() = navigationOwnerViewModel().serviceProvider

interface NavigationOwner {
    val state: NavigationState
    val serviceProvider: ScopedServiceProvider
}

class NavigationOwnerViewModel : NavigationOwner, ViewModel() {
    override val state = DefaultNavigationState()
    override val serviceProvider = DefaultScopedServiceProvider(state)
}