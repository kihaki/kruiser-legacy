package de.gaw.kruiser.example.wizard

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.destination.Screen

interface WizardDestination : Destination {
    override fun build(): WizardScreen
}
interface WizardScreen : Screen {
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

private class WizardStateImpl : WizardState {
    override var title by mutableStateOf("")
    override var progress by mutableFloatStateOf(0f)
}

private val LocalWizardState = compositionLocalOf<WizardState?> { null }

@SuppressLint("ComposableNaming")
@Composable
fun BackstackState.provideWizardState(content: @Composable WizardState.() -> Unit) {
    val wizardState = remember(this) { WizardStateImpl() }
    CompositionLocalProvider(LocalWizardState provides wizardState) {
        wizardState.content()
    }
}

@Composable
fun WizardDestination.rememberLocalWizardState(): WizardState = LocalWizardState.currentOrThrow