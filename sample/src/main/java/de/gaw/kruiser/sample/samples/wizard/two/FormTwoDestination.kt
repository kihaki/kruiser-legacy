package de.gaw.kruiser.sample.samples.wizard.two

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormModel
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormModelFactory
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormScope
import de.gaw.kruiser.sample.transition.HorizontalCardStackTransition
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.scopedService
import de.gaw.kruiser.state.preview.PreviewNavigationState
import de.gaw.kruiser.ui.singletopstack.transition.LocalEntryExitTransitionTracker
import de.gaw.kruiser.ui.singletopstack.transition.PreviewEntryExitTransitionTracker
import kotlinx.coroutines.flow.update

object FormTwoDestination : Destination {
    override fun build(): Screen = FormTwoScreen()
}

private class FormTwoScreen : Screen {
    override val destination: Destination = FormTwoDestination

    @Composable
    override fun Content() = HorizontalCardStackTransition {
        val sharedForm = scopedService(
            factory = SharedFormModelFactory,
            scope = SharedFormScope,
        )
        val formModel = scopedService(factory = FormTwoModelFactory(sharedForm))
        FormTwo(model = formModel)
    }
}

@Composable
private fun FormTwo(
    model: FormTwoModel,
) {
    val name by model.name.collectAsState()
    val address by model.address.collectAsState()
    FormTwo(
        name = name,
        address = address,
        onAddressChange = { text -> model.address.update { text } },
    )
}

@Composable
private fun FormTwo(
    name: String,
    address: String,
    onAddressChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Surface(
            modifier = Modifier.weight(1f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val displayedName by remember {
                    derivedStateOf {
                        name.takeIf { it.isNotBlank() } ?: "(none selected)"
                    }
                }
                Text("Your selected name is $displayedName, please enter your address:")
                OutlinedTextField(
                    value = address,
                    onValueChange = onAddressChange,
                )
            }
        }
    }
}

@Preview
@Composable
private fun FormTwoPreview() = MaterialTheme {
    CompositionLocalProvider(
        LocalNavigationState provides PreviewNavigationState(),
        LocalEntryExitTransitionTracker provides PreviewEntryExitTransitionTracker(),
    ) {
        FormTwo(model = FormTwoModel(SharedFormModel()))
    }
}