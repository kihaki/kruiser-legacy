package de.gaw.kruiser.sample.samples.wizard.shared

import de.gaw.kruiser.sample.samples.wizard.FormDestination
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceScope
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.currentStack

/**
 * Scope to keep the service alive for as long as there is *any* form page on the stack.
 */
object SharedFormScope : ServiceScope {
    override fun isAlive(state: NavigationState): Boolean = state.currentStack.any { destination ->
        destination is FormDestination
    }
}