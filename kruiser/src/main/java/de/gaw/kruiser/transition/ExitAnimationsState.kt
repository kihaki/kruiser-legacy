package de.gaw.kruiser.transition

import androidx.compose.runtime.compositionLocalOf
import de.gaw.kruiser.destination.Destination
import kotlinx.coroutines.flow.StateFlow

val LocalExitAnimationsState =
    compositionLocalOf<ExitAnimationsState> { error("No ExitAnimationsRenderState provided.") }

/**
 * Caches removed [Destination]s that want to play an exit animation.
 */
interface ExitAnimationsState {
    val onScreenDestinations: StateFlow<List<Destination>>
    val destinationsRunningExitAnimations: StateFlow<Set<Destination>>

    fun onExitAnimationCompleted(destination: Destination)

    fun registerExitAnimationSupport(destination: Destination)
    fun unregisterExitAnimationSupport(destination: Destination)
}

