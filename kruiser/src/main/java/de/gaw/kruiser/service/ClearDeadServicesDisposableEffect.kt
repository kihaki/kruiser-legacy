package de.gaw.kruiser.service

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import de.gaw.kruiser.android.LocalScopedServiceProvider

/**
 * Removes dead services when this composable leaves the composition.
 * This means that the [ScopedServiceProvider] will check and clear any services that are no longer
 * in use when the composable leaves the composition.
 */
@Composable
fun ClearDeadServicesDisposableEffect(
    key: Any = Unit,
    scopedServiceProvider: ScopedServiceProvider = LocalScopedServiceProvider.current,
) {
    DisposableEffect(key, scopedServiceProvider) {
        onDispose {
            scopedServiceProvider.clearDeadServices()
        }
    }
}