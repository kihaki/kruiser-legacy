package de.gaw.kruiser.example.wizard

import de.gaw.kruiser.destination.Destination

interface WizardDestination : Destination {
    val wizardState: WizardState
}

interface ModalTransition

interface WizardState {
    val title: String
    val progress: Float
}

data class DefaultWizardState(
    override val title: String = "",
    override val progress: Float = 0f,
) : WizardState