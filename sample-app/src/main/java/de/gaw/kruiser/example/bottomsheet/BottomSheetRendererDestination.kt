package de.gaw.kruiser.example.bottomsheet

import androidx.compose.runtime.Composable
import de.gaw.kruiser.backstack.ui.transition.BottomCardTransition
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
object BottomSheetRendererDestination : AndroidDestination {

    private fun readResolve(): Any = BottomSheetRendererDestination

    override fun build(): Screen = object : Screen {

        @Composable
        override fun Content() = BottomCardTransition {
            val backstack = rememberSaveableBackstack(
                backstackId = "bottom-sheet-renderer-backstack",
                initial = listOf(
                    RegularPageDestination("First Page"),
                )
            )
            BackstackRendererWithBottomSheet(backstack = backstack)
        }
    }
}