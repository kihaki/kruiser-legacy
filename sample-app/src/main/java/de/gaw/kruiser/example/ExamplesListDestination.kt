package de.gaw.kruiser.example

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstackState
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.bottomsheet.BottomSheetRendererDestination
import de.gaw.kruiser.example.wizard.destination.exampleWizardDestinations
import kotlinx.parcelize.Parcelize

@Parcelize
object ExamplesListDestination : AndroidDestination {
    private fun readResolve(): Any = ExamplesListDestination

    override fun build(): Screen = object : Screen {

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        override fun Content() {
            Scaffold(
                topBar = { TopAppBar(title = { Text("Kruiser Samples") }) }
            ) {
                val backstack = LocalMutableBackstackState.currentOrThrow
                LazyColumn(contentPadding = it) {
                    item {
                        ListItem(
                            modifier = Modifier
                                .clickable { backstack.push(exampleWizardDestinations.first()) },
                            headlineContent = { Text("Wizard") },
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

