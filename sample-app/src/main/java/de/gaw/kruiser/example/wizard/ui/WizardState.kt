package de.gaw.kruiser.example.wizard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * A [CompositionLocal] holding the current [WizardStateHolder].
 */
val LocalWizardStateHolder = compositionLocalOf { WizardStateHolder() }

/**
 * A holder for the current [WizardState].
 */
interface WizardStateHolder {
    var wizardState: WizardState
}

/**
 * Creates a new [WizardStateHolder].
 */
fun WizardStateHolder(): WizardStateHolder = DefaultWizardStateHolder()

/**
 * A default implementation of [WizardStateHolder].
 */
private class DefaultWizardStateHolder : WizardStateHolder {
    override var wizardState: WizardState by mutableStateOf(WizardState())
}

/**
 * The state of a wizard for a current page.
 */
data class WizardState(
    val title: String = "",
    val progress: Float = 0f,
)

/**
 * Connects the given [wizardState] to the current [WizardStateHolder].
 */
@Composable
fun ConnectWizardState(wizardState: WizardState) {
    LocalWizardStateHolder.current.wizardState = wizardState
}