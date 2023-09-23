package de.gaw.kruiser.service.scope

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceScope

/**
 * The service should be alive for as long as the provided destination is anywhere on the stack.
 */
data class DestinationScope(val destination: Destination) : ServiceScope {
    override fun isAlive(destinations: List<Destination>): Boolean =
        destinations.contains(destination)
}