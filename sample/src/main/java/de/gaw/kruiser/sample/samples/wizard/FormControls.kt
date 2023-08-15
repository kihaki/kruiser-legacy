package de.gaw.kruiser.sample.samples.wizard

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
import de.gaw.kruiser.previousDestination
import de.gaw.kruiser.remoteui.RemoteUi
import de.gaw.kruiser.remoteui.RemoteUiPlaceholder
import de.gaw.kruiser.sample.samples.wizard.two.FormTwoDestination
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.state.push

const val KEY_FORM_CONTROLS = "key-form-controls"

/**
 * Actual persistent controls of the current form (the buttons at the bottom).
 */
@Composable
fun FormControls() = RemoteUi<FormDestination>(
    key = KEY_FORM_CONTROLS,
) {
    val navigationState = LocalNavigationState.current
    val stack by navigationState.collectCurrentStack()
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
                onClick = { navigationState.push(FormTwoDestination) }
            ) {
                Text("Next")
            }
        }
    }
}

/**
 * Placeholder for the position of the persistent form controls (where the bottom buttons are placed).
 * This goes into the [FormDestination]s composables.
 */
@Composable
fun FormControlsPlaceholder() =
    RemoteUiPlaceholder(key = KEY_FORM_CONTROLS) { stack: List<Destination> ->
        stack.previousDestination() !is FormDestination
    }