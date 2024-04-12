package de.gaw.kruiser.example.wizard

import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.MutableBackstackState
import de.gaw.kruiser.example.wizard.destination.exampleWizardDestinations

fun MutableBackstackState.popWizard() = mutate { popWizard() }

fun BackstackEntries.popWizard() =
    filterNot { exampleWizardDestinations.contains(it.destination) }