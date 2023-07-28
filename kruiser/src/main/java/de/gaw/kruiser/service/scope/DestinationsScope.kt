package de.gaw.kruiser.service.scope

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceScope
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.currentStack

/**
 * The service should be alive for as long as any of the provided destinations are anywhere on
 * the stack.
 */
data class DestinationsScope(val destinations: Set<Destination>) : ServiceScope {
    override fun isAlive(state: NavigationState): Boolean = state.currentStack.any { destination ->
        destinations.contains(destination)
    }
}