package de.gaw.kruiser.sample.samples.push

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import de.gaw.kruiser.sample.transition.VerticalCardStackTransition
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.service
import kotlin.random.Random


data class PushAndPopScreenVertical(
    val index: Int,
    override val destination: Destination,
) : Screen {

    @Composable
    override fun Content() = VerticalCardStackTransition {
        val model = service(PushAndPopScreenModelFactory(index))
        PushAndPop(
            title = "Screen $index",
            onPushDefault = model::onPushDefault,
            onPushCustom = model::onPushCustom,
            onGoToFirst = model::onGoToFirst,
        )
    }
}

data class PushAndPopScreenHorizontal(
    val index: Int,
    override val destination: Destination,
) : WizardScreen {

    @Composable
    override fun Content() = HorizontalCardStackTransition {
        val model = service(PushAndPopScreenModelFactory(index))
        Column {
            PushAndPop(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                title = "Screen $index",
                onPushDefault = model::onPushDefault,
                onPushCustom = model::onPushCustom,
                onGoToFirst = model::onGoToFirst,
            )
            WizardControlsPlaceholder()
        }
    }
}

@Composable
private fun PushAndPop(
    title: String,
    onPushDefault: () -> Unit,
    onPushCustom: () -> Unit,
    onGoToFirst: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = rememberSaveable(saver = colorSaver()) { Color.randomSoft() }

    Surface(
        modifier = modifier,
        color = backgroundColor,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(title)
            Spacer(modifier = Modifier.size(24.dp))
            ElevatedButton(onClick = onPushDefault) {
                Text("Push with default animation")
            }
            Spacer(modifier = Modifier.size(24.dp))
            ElevatedButton(onClick = onPushCustom) {
                Text("Push with custom animation")
            }
            Spacer(modifier = Modifier.size(24.dp))
            ElevatedButton(onClick = onGoToFirst) {
                Text("Go back to first screen")
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
        onPushCustom = {},
        onGoToFirst = {},
    )
}