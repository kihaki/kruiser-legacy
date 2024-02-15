package de.gaw.kruiser.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.results.rememberResult
import de.gaw.kruiser.backstack.ui.transition.CardTransition
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.bottomsheet.BottomSheetRendererDestination
import de.gaw.kruiser.example.wizard.WizardExampleDestination
import de.gaw.kruiser.example.wizard.WizardExampleDestination.WizardResult
import kotlinx.parcelize.Parcelize

@Parcelize
object ExampleListDestination : AndroidDestination {
    private fun readResolve(): Any = ExampleListDestination

    override fun build(): Screen = object : Screen {

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        override fun Content() = CardTransition {
            Scaffold(
                topBar = { TopAppBar(title = { Text("Kruiser Samples") }) }
            ) {
                val backstack = LocalMutableBackstack.currentOrThrow
                LazyColumn(contentPadding = it) {
                    item {
                        ListItem(
                            modifier = Modifier
                                .clickable { backstack.push(WizardExampleDestination) },
                            headlineContent = { Text("Wizard") },
                            supportingContent = {
                                val result by rememberResult<WizardResult>()
                                result?.let { wizardResult ->
                                    Text(
                                        text = "${wizardResult.name?.takeUnless { it.isBlank() } ?: "(No Name)"} aka. ${wizardResult.nickname?.takeUnless { it.isBlank() } ?: "(No Nickname)"}",
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                        )
                        ListItem(
                            modifier = Modifier
                                .clickable { backstack.push(BottomSheetRendererDestination) },
                            headlineContent = { Text("Custom Renderer with BottomSheet") },
                        )
                    }
                }
            }
        }
    }
}

