package de.gaw.kruiser.destinationgroup

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState

interface DestinationGroupContext {
    val state: NavigationState
}

interface DestinationGroup {
    fun DestinationGroupContext.isIncluded(destination: Destination): Boolean
}

interface WizardDestination : Destination