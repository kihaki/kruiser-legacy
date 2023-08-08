package de.gaw.kruiser.sample.samples.push

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.remoteui.RemoteUi
import de.gaw.kruiser.remoteui.RemoteUiPlaceholder
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.pop

private const val WIZARD_CONTROLS_KEY = "remote-ui-key:wizard-controls"

@Composable
fun WizardControls() {
    val navigationState = LocalNavigationState.current
    val stack by navigationState.collectCurrentStack()
    val zIndex by remember(navigationState) {
        derivedStateOf {
            stack.map { it.build() }.indexOfLast { it is WizardScreen }.toFloat()
        }
    }
    RemoteUi(
        key = WIZARD_CONTROLS_KEY,
        zIndex = zIndex,
    ) {
        val canGoBack by remember(navigationState) { derivedStateOf { stack.size >= 3 } }
        Surface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                AnimatedVisibility(
                    visible = canGoBack,
                    modifier = Modifier
                        .weight(1f),
                ) {
                    ElevatedButton(onClick = navigationState::pop) {
                        Text("Previous")
                    }
                }
                ElevatedButton(
                    modifier = Modifier
                        .weight(1f)
                        .animateContentSize(),
                    onClick = { }
                ) {
                    Text("Next")
                }
            }
        }
    }
}

interface WizardScreen : Screen

@Composable
fun WizardScreen.WizardControlsPlaceholder() = RemoteUiPlaceholder(
    key = WIZARD_CONTROLS_KEY,
) { stack: List<Destination> ->
    val previousScreen: Screen? = stack.dropLast(1).lastOrNull()?.build()
    previousScreen !is WizardScreen
}