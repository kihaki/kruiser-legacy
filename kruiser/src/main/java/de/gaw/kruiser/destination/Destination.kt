package de.gaw.kruiser.destination

import de.gaw.kruiser.screen.Screen

interface Destination {
    fun build(): Screen
}