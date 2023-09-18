package de.gaw.kruiser.service

import androidx.compose.runtime.Composable
import de.gaw.kruiser.android.LocalScopedServiceProvider
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceFactory
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceScope
import de.gaw.kruiser.service.scope.DestinationScope
import kotlin.reflect.KClass


@Composable
inline fun <reified T : Any> Screen.scopedService(
    factory: ServiceFactory<T>,
    serviceProvider: ScopedServiceProvider = LocalScopedServiceProvider.current,
) = scopedService(
    scope = DestinationScope(destination),
    factory = factory,
    serviceProvider = serviceProvider,
)


@Composable
inline fun <reified T : Any> scopedService(
    factory: ServiceFactory<T>,
    scope: ServiceScope,
    serviceProvider: ScopedServiceProvider = LocalScopedServiceProvider.current,
) = serviceProvider.scopedService(scope = scope, factory = factory)

@Composable
@Deprecated("Careful! Don't use this yet, this will always return the first instance of the service if the service has parameters!")
private inline fun <reified T : Any> Screen.scopedService(
    scope: ServiceScope = DestinationScope(destination),
    serviceProvider: ScopedServiceProvider,
    noinline producer: () -> T,
) = scopedService(
    factory = DefaultServiceFactory(T::class, producer),
    scope = scope,
    serviceProvider = serviceProvider,
)

private class DefaultServiceFactory<T : Any>(
    private val clazz: KClass<T>,
    private val factory: () -> T,
) : ServiceFactory<T> {

    override fun hashCode(): Int = clazz.hashCode()

    override fun equals(other: Any?): Boolean =
        (other as? DefaultServiceFactory<*>)?.clazz == this.clazz

    override fun ScopedServiceProvider.ServiceContext.create(): T = factory()
}