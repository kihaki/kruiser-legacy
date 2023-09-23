package de.gaw.kruiser.sample.samples.wizard.shared

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.sample.samples.wizard.FormDestination
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceScope

/**
 * Scope to keep the service alive for as long as there is *any* form page on the stack.
 */
object SharedFormScope : ServiceScope {
    override fun isAlive(destinations: List<Destination>): Boolean =
        destinations.any { destination ->
            destination is FormDestination
        }
}