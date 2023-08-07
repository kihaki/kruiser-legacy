package de.gaw.kruiser.destinationgroup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState

interface DestinationGroupContext {
    val state: NavigationState
}

interface DestinationGroup {
    fun DestinationGroupContext.isIncluded(destination: Destination): Boolean

    @Composable
    fun GroupContent() {}
}

interface WizardDestination : DestinationGroup {
    @Composable
    override fun GroupContent() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
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
}