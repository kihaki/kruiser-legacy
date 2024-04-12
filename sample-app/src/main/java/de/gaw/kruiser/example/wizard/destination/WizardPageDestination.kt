package de.gaw.kruiser.example.wizard.destination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.transition.ModalTransition
import de.gaw.kruiser.example.viewmodel.LifecycleLoggingViewModel
import de.gaw.kruiser.example.wizard.WizardDestination
import de.gaw.kruiser.example.wizard.ui.ConnectWizardState
import de.gaw.kruiser.example.wizard.ui.WizardEntryPageContent
import de.gaw.kruiser.example.wizard.ui.WizardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.Parcelize

@Parcelize
object WizardNameDestination :
    AndroidDestination,
    WizardDestination,
    ModalTransition {
    private fun readResolve(): Any = WizardNameDestination

    class WizardNameViewModel(
        destination: WizardDestination,
    ) : LifecycleLoggingViewModel() {
        val wizardState = MutableStateFlow(
            WizardState(
                title = "Your Name",
                progress = exampleWizardDestinations.progressOf(destination),
            )
        )
    }

    override fun build() = object : Screen {
        @Composable
        override fun Content() {
            val viewModel = viewModel { WizardNameViewModel(this@WizardNameDestination) }
            val wizardState by viewModel.wizardState.collectAsState()
            ConnectWizardState(wizardState)

            WizardEntryPageContent(wizardState)
        }
    }
}

