package de.gaw.kruiser.screen

import androidx.compose.runtime.Composable
import de.gaw.kruiser.destination.Destination

interface Screen {
    val destination: Destination

    @Composable
    fun Content()
}