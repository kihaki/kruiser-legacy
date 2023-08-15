package de.gaw.kruiser.service.preview

import de.gaw.kruiser.service.DefaultScopedServiceProvider.DefaultServiceContext
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceFactory
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceScope
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.preview.PreviewNavigationState

class PreviewScopedServiceProvider(
    private val navigationState: NavigationState = PreviewNavigationState(),
) : ScopedServiceProvider {
    override fun <T : Any> scopedService(scope: ServiceScope, factory: ServiceFactory<T>): T = with(factory) {
        DefaultServiceContext(navigationState).create()
    }

    override fun clearDeadServices() {
        // No Op
    }
}