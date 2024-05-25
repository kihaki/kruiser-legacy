package de.gaw.kruiser.example

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstackState
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Preview
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.bottomsheet.BottomSheetRendererDestination
import de.gaw.kruiser.example.tab.TabDestination
import de.gaw.kruiser.example.wizard.destination.exampleWizardDestinations
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import kotlinx.parcelize.Parcelize

/**
 * A destination that shows a list of examples to navigate to.
 */
@Parcelize
object ExamplesListDestination : AndroidDestination {
    private fun readResolve(): Any = ExamplesListDestination

    override fun build(): Screen = object : Screen {

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        override fun Content() {
            Scaffold(
                topBar = { TopAppBar(title = { Text("Kruiser Samples") }) }
            ) { contentPadding ->
                val backstack = LocalMutableBackstackState.currentOrThrow

                LazyColumn(contentPadding = contentPadding) {
                    ExampleItem(
                        title = "Wizard",
                        onClick = { backstack.push(exampleWizardDestinations.first()) },
                    )
                    ExampleItem(
                        title = "Custom Renderer with BottomSheet",
                        onClick = { backstack.push(BottomSheetRendererDestination) },
                    )
                    ExampleItem(
                        title = "Tabs with nested Navigation",
                        onClick = { backstack.push(TabDestination) },
                    )
                }
            }
        }
    }
}

@Suppress("FunctionName")
private fun LazyListScope.ExampleItem(
    title: String,
    onClick: () -> Unit,
) = item(key = title) {
    ListItem(
        modifier = Modifier
            .clickable(onClick = onClick),
        headlineContent = { Text(title) },
    )
}

@Preview
@Composable
private fun ExamplesListDestinationPreview() = KruiserSampleTheme {
    ExamplesListDestination.Preview()
}

