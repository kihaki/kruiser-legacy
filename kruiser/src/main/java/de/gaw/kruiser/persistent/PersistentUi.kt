package de.gaw.kruiser.persistent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

object PersistentUi {
    var size by mutableStateOf(DpSize.Zero)
    var position by mutableStateOf<Offset?>(null)
}

@Composable
fun PersistentUiSlot() {
    Box(
        modifier = Modifier
            .size(PersistentUi.size)
            .onGloballyPositioned {
                PersistentUi.position = it.localToRoot(Offset.Zero)
            }
    ) {
        DisposableEffect(Unit) {
            onDispose {
                PersistentUi.position = null
            }
        }
    }
}

@Composable
fun PersistentUi() {
    val density = LocalDensity.current
    PersistentUi.position?.let { position ->
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .wrapContentHeight()
                .offset {
                    IntOffset(position.x.toInt(), position.y.toInt())
                }
                .onGloballyPositioned {
                    PersistentUi.size =
                        with(density) { DpSize(it.size.width.toDp(), it.size.height.toDp()) }
                }
                .padding(all = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ElevatedButton(onClick = { /*TODO*/ }) {
                Text("Previous")
            }
            ElevatedButton(onClick = { /*TODO*/ }) {
                Text("Next")
            }
        }
    }
}