package de.gaw.kruiser.example.wizard.destination

import de.gaw.kruiser.destination.Destination

val exampleWizardDestinations: List<Destination> = listOf(
    WizardNameDestination,
    WizardNicknameDestination,
    WizardCompletionDestination,
)

fun List<Destination>.progressOf(destination: Destination) =
    (indexOf(destination) + 1) / size.toFloat()
