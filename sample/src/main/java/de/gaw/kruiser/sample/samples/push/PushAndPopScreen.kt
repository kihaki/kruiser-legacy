package de.gaw.kruiser.sample.samples.push

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.sample.saver.colorSaver
import de.gaw.kruiser.sample.theme.KruiserPreviewTheme
import de.gaw.kruiser.sample.transition.HorizontalCardStackTransition
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.scopedService
import kotlin.random.Random

data class PushAndPopScreenHorizontal(
    val index: Int,
    override val destination: Destination,
) : Screen {

    @Composable
    override fun Content() = HorizontalCardStackTransition {
        val model = scopedService(PushAndPopScreenModelFactory(index))
        PushAndPop(
            modifier = Modifier
                .fillMaxWidth(),
            title = "Screen $index",
            onPushDefault = model::onPushDefault,
            onShowBottomSheet = model::onShowBottomSheetDestination,
            onShowForm = model::onPushForm,
            onGoToFirst = model::onGoToFirst,
        )
    }
}

@Composable
private fun PushAndPop(
    title: String,
    onPushDefault: () -> Unit,
    onShowBottomSheet: () -> Unit,
    onShowForm: () -> Unit,
    onGoToFirst: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = rememberSaveable(saver = colorSaver()) { Color.randomSoft() }

    Surface(
        modifier = modifier,
        color = backgroundColor,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(title)
                ElevatedButton(onClick = onPushDefault) {
                    Text("Push another Screen")
                }
                ElevatedButton(onClick = onShowForm) {
                    Text("Open multi page form example")
                }
                ElevatedButton(onClick = onShowBottomSheet) {
                    Text("Show Bottom Sheet")
                }
                ElevatedButton(onClick = onGoToFirst) {
                    Text("Go back to first screen")
                }
            }
        }
    }
}

private fun Color.Companion.randomSoft() = Color(
    Random.nextFloat() * .5f + .5f,
    Random.nextFloat() * .5f + .5f,
    Random.nextFloat() * .5f + .5f,
    1f,
)

@Preview
@Composable
private fun PushAndPopScreenPreview() = KruiserPreviewTheme {
    PushAndPop(
        modifier = Modifier.fillMaxSize(),
        title = "Screen 1",
        onPushDefault = {},
        onShowBottomSheet = {},
        onShowForm = {},
        onGoToFirst = {},
    )
}