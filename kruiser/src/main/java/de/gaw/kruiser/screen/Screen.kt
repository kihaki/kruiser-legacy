package de.gaw.kruiser.screen

import androidx.compose.runtime.Composable
import de.gaw.kruiser.destination.Destination

interface Screen {
    /**
     * The [Destination] associated with this [Screen],
     * used as a key for this [Screen] in some places.
     */
    val destination: Destination

    /**
     * If the [Screen] is declared as translucent, the [Screen] right below it on the stack
     * will still be rendered, this is intended for partly transparent [Screen]s such as Dialog
     * styled [Screen]s.
     */
    val isTranslucent: Boolean

    @Composable
    fun Content()
}