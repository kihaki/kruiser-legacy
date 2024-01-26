package de.gaw.kruiser.example

import androidx.compose.runtime.Composable
import de.gaw.kruiser.backstack.ui.Backstack
import de.gaw.kruiser.backstack.ui.transition.orchestrator.transition.CardTransition
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object NestedBackstackExampleDestination : AndroidDestination {
    private fun readResolve(): Any = NestedBackstackExampleDestination

    override fun build(): Screen = object : Screen {
        private val initialDestination = EmojiDestination(emojis.first())
        @Composable
        override fun Content() = CardTransition {
            Backstack(backstack = rememberSaveableBackstack(listOf(initialDestination)))
        }
    }
}

