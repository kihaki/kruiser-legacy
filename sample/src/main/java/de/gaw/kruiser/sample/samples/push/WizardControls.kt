package de.gaw.kruiser.sample.samples.push

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.remoteui.RemoteUi
import de.gaw.kruiser.remoteui.RemoteUiPlaceholder
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.pop

private const val WIZARD_CONTROLS_KEY = "remote-ui-key:wizard-controls"

@Composable
fun WizardControls() = RemoteUi(key = WIZARD_CONTROLS_KEY) {
    val navigationState = LocalNavigationState.current
    val stack by navigationState.collectCurrentStack()
    val canGoBack = remember(navigationState) { stack.size >= 2 }
    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            AnimatedVisibility(visible = canGoBack) {
                ElevatedButton(onClick = navigationState::pop) {
                    Text("Previous")
                }
            }
            ElevatedButton(
                onClick = { }
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun WizardControlsPlaceholder() = RemoteUiPlaceholder(key = WIZARD_CONTROLS_KEY)