package de.gaw.kruiser.screen

import androidx.compose.runtime.Composable
import de.gaw.kruiser.destination.Destination

interface ProvidesSavedStateKey {
    /**
     * The key used to save this to a saved state.
     * Must be of a type saveable in an Android bundle.
     */
    val savedStateKey: Any
}

interface ProvidesComposeKey {
    val composeKey: Any
}

interface Screen :
    ProvidesSavedStateKey,
    ProvidesComposeKey {

        /**
     * The [Destination] associated with this [Screen].
     */
    val destination: Destination

    /**
     * The key used to save this [Screen] to a saved state.
     */
    override val savedStateKey: Any get() = destination

    /**
     * The key used to signal to jetpack compose if the [Content] contains the same composables.
     */
    override val composeKey: Any get() = destination

    @Composable
    fun Content()
}