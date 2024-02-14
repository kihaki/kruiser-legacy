package de.gaw.kruiser.example

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.gaw.kruiser.backstack.push
import de.gaw.kruiser.backstack.results.rememberResult
import de.gaw.kruiser.backstack.ui.transition.CardTransition
import de.gaw.kruiser.backstack.ui.util.LocalMutableBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.example.wizard.WizardExampleDestination
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
                        val result: WizardExampleDestination.Result? by rememberResult()
                        LaunchedEffect(result) {
                            Log.v("ScreenResult", "WizardResult is $result")
                        }
                        ListItem(
                            modifier = Modifier
                                .clickable { backstack.push(WizardExampleDestination) },
                            headlineContent = { Text("Wizard") },
                        )
                    }
                }
            }
        }
    }
}

