package de.gaw.kruiser.destination

import de.gaw.kruiser.screen.Screen
import java.io.Serializable

interface Destination : Serializable {
    fun build(): Screen
}