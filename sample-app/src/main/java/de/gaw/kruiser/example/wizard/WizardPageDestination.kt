package de.gaw.kruiser.example.wizard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.backstack.ui.transition.CardTransition
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class WizardPageDestination(val page: Int) : AndroidDestination {
    override fun build(): Screen = object : Screen {
        @Composable
        override fun Content() = CardTransition {
            Surface(
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$page",
                        style = MaterialTheme.typography.displayLarge,
                    )
                }
            }
        }
    }
}