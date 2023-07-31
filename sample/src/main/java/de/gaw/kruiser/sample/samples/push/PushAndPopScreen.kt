package de.gaw.kruiser.sample.samples.push

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import de.gaw.kruiser.destinationgroup.WizardDestination
import de.gaw.kruiser.sample.saver.colorSaver
import de.gaw.kruiser.sample.theme.KruiserPreviewTheme
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.service
import kotlin.random.Random

data class PushAndPopScreen(
    val index: Int,
    override val destination: Destination,
) : Screen {

    @Composable
    override fun Content() {
        val model = service(PushAndPopScreenModelFactory(index))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wizardControlsPadding(),
        ) {
            PushAndPop(
                title = "Screen $index",
                onPushDefault = model::onPushDefault,
                onPushCustom = model::onPushCustom,
                onGoToFirst = model::onGoToFirst,
            )
        }
    }
}

context(Screen)
fun Modifier.wizardControlsPadding() = when (this@Screen.destination) {
    is WizardDestination -> padding(bottom = 72.dp)
    else -> this
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