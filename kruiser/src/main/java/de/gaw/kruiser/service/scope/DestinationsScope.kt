package de.gaw.kruiser.service.scope

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceScope

/**
 * The service should be alive for as long as any of the provided destinations are anywhere on
 * the stack.
 */
data class DestinationsScope(val destinations: Set<Destination>) : ServiceScope {
    override fun isAlive(destinations: List<Destination>): Boolean =
        destinations.any { destination ->
            destinations.contains(destination)
        }
}