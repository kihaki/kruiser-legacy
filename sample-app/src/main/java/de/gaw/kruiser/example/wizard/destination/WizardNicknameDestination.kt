package de.gaw.kruiser.example.wizard.destination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Preview
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.transition.ModalTransition
import de.gaw.kruiser.example.viewmodel.LifecycleLoggingViewModel
import de.gaw.kruiser.example.wizard.WizardDestination
import de.gaw.kruiser.example.wizard.ui.ConnectWizardState
import de.gaw.kruiser.example.wizard.ui.WizardEntryPageContent
import de.gaw.kruiser.example.wizard.ui.WizardState
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.Parcelize

@Parcelize
object WizardNicknameDestination :
    AndroidDestination,
    WizardDestination,
    ModalTransition {
    private fun readResolve(): Any = WizardNicknameDestination

    class WizardNicknameViewModel(
        destination: WizardDestination,
    ) : LifecycleLoggingViewModel() {
        val wizardState = MutableStateFlow(
            WizardState(
                title = "Your Nickname",
                progress = exampleWizardDestinations.progressOf(destination),
            )
        )
    }

    override fun build() = object : Screen {

        @Composable
        override fun Content() {
            val viewModel = viewModel {
                WizardNicknameViewModel(this@WizardNicknameDestination)
            }
            val wizardState by viewModel.wizardState.collectAsState()
            ConnectWizardState(wizardState)

            WizardEntryPageContent(wizardState)
        }
    }
}

@Preview
@Composable
private fun WizardNicknameDestinationPreview() = KruiserSampleTheme {
    WizardNicknameDestination.Preview()
}