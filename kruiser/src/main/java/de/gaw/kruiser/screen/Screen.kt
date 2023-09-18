package de.gaw.kruiser.screen

import androidx.compose.runtime.Composable
import de.gaw.kruiser.destination.Destination

interface Screen {
    /**
     * The [Destination] associated with this [Screen],
     * used as a key for this [Screen] in some places.
     */
    val destination: Destination

    @Composable
    fun Content()
}