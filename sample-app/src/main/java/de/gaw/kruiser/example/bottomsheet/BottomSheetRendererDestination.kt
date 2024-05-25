package de.gaw.kruiser.example.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.gaw.kruiser.backstack.ui.util.rememberSaveableBackstack
import de.gaw.kruiser.destination.AndroidDestination
import de.gaw.kruiser.destination.Preview
import de.gaw.kruiser.destination.Screen
import de.gaw.kruiser.ui.theme.KruiserSampleTheme
import kotlinx.parcelize.Parcelize

@Parcelize
object BottomSheetRendererDestination : AndroidDestination {

    private fun readResolve(): Any = BottomSheetRendererDestination

    override fun build(): Screen = object : Screen {

        @Composable
        override fun Content() {
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

@Preview
@Composable
private fun BottomSheetRendererDestinationPreview() = KruiserSampleTheme {
    BottomSheetRendererDestination.Preview()
}