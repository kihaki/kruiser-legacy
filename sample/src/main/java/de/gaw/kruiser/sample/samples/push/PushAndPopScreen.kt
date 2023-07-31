package de.gaw.kruiser.sample.samples.push

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.destination.Destination
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
        PushAndPop(
            modifier = Modifier.fillMaxSize(),
            title = "Screen $index",
            onPushDefault = model::onPushDefault,
            onPushCustom = model::onPushCustom,
        )
    }
}

@Composable
private fun PushAndPop(
    title: String,
    onPushDefault: () -> Unit,
    onPushCustom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = remember {
        Color(
            Random.nextFloat() * .5f + .5f,
            Random.nextFloat() * .5f + .5f,
            Random.nextFloat() * .5f + .5f,
            1f,
        )
    }

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
        }
    }
}

@Preview
@Composable
private fun PushAndPopScreenPreview() = KruiserPreviewTheme {
    PushAndPop(
        modifier = Modifier.fillMaxSize(),
        title = "Screen 1",
        onPushDefault = {},
        onPushCustom = {},
    )
}