package de.gaw.kruiser.service

import android.util.Log
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceContext
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceFactory
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceScope
import de.gaw.kruiser.state.NavigationState
import java.io.Closeable

interface ScopedServiceProvider {
    interface ServiceScope {
        /**
         * Returns true if the service should be kept alive with this particular list of destinations
         */
        fun isAlive(destinations: List<Destination>): Boolean
    }

    interface ServiceFactory<T : Any> {
        fun ServiceContext.create(): T
    }

    interface ServiceContext {
        val navigationState: NavigationState
    }

    /**
     * Returns a service that is scoped to the specified [scope].
     */
    fun <T : Any> scopedService(
        scope: ServiceScope,
        factory: ServiceFactory<T>,
    ): T

    /**
     * Checks the currently cached services and removes those that should not be alive.
     */
    fun clearDeadServices(destinations: List<Destination>)
}

class DefaultScopedServiceProvider(
    private val state: NavigationState,
) : ScopedServiceProvider {
    private var scopes = mapOf<ServiceFactory<*>, Set<ServiceScope>>()
    private var instances = mapOf<ServiceFactory<*>, Any>()

    class DefaultServiceContext(
        override val navigationState: NavigationState,
    ) : ServiceContext

    override fun <T : Any> scopedService(
        scope: ServiceScope,
        factory: ServiceFactory<T>,
    ): T {
        // Step 1: Update the service scopes to include the new scope
        val currentServiceScope = scopes
            .getOrDefault(factory, emptySet())

        val updatedServiceScope = currentServiceScope
            .toMutableSet()
            .apply {
                add(scope)
            }

        scopes = scopes
            .toMutableMap()
            .apply {
                put(factory, updatedServiceScope)
            }

        // Step 2: Return a cached instance or, if that does not yet exist, create, cache and return a new instance
        val instance: T
        instances = instances
            .toMutableMap()
            .apply {
                @Suppress("UNCHECKED_CAST")
                instance = getOrPut(factory) {
                    with(factory) {
                        DefaultServiceContext(state).create().also {
                            Log.v("Service", "Creating Service $it")
                        }
                    }
                } as T
            }

        return instance
    }

    override fun clearDeadServices(destinations: List<Destination>) {
        // 1. Adjust *inner scopes set* to not include dead scopes (empty sets will be left for dead services!)
        scopes = scopes.mapValues { (_, scopes) ->
            val liveScopes = scopes.filter { scope -> scope.isAlive(destinations) }.toSet()
            liveScopes
        }

        // 2. Remove dead instances
        val updatedInstances = instances.toMutableMap()
        scopes = scopes.filter { (factory, scopes) ->
            val serviceIsAlive = scopes.isNotEmpty()
            if (!serviceIsAlive) {
                Log.v("Service", "Closing Service ${instances[factory]}")
                // Notify the service of its demise
                (instances[factory] as? Closeable)?.close()
                // Remove it from the instances so the garbage collector can assassinate it in peace
                updatedInstances.remove(factory)
            }
            serviceIsAlive
        }
        instances = updatedInstances

        // 3. Clear dead scopes too
        scopes = scopes.filter { (_, scopes) ->
            scopes.isNotEmpty()
        }
    }
}