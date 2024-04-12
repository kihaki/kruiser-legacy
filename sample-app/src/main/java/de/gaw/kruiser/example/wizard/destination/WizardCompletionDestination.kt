package de.gaw.kruiser.example.wizard.destination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.transition.ModalTransition
import de.gaw.kruiser.example.viewmodel.LifecycleLoggingViewModel
import de.gaw.kruiser.example.wizard.WizardDestination
import de.gaw.kruiser.example.wizard.ui.ConnectWizardState
import de.gaw.kruiser.example.wizard.ui.WizardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.Parcelize

@Parcelize
object WizardCompletionDestination :
    AndroidDestination,
    WizardDestination,
    ModalTransition {
    private fun readResolve(): Any = WizardCompletionDestination

    class WizardCompletionViewModel(
        destination: WizardDestination,
    ) : LifecycleLoggingViewModel() {
        val wizardState = MutableStateFlow(
            WizardState(
                title = "Completed?",
                progress = exampleWizardDestinations.progressOf(destination),
            )
        )
    }

    override fun build() = object : Screen {

        @Composable
        override fun Content() {
            val viewModel = viewModel {
                WizardCompletionViewModel(this@WizardCompletionDestination)
            }
            val wizardState by viewModel.wizardState.collectAsState()
            ConnectWizardState(wizardState)

            Surface(
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Is this correct?",
                            style = MaterialTheme.typography.displayLarge,
                        )
                    }
                }
            }
        }
    }
}