package de.gaw.kruiser.sample.samples.wizard.one

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormScope
import de.gaw.kruiser.sample.samples.wizard.FormControlsPlaceholder
import de.gaw.kruiser.sample.samples.wizard.FormDestination
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormModel
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormModelFactory
import de.gaw.kruiser.sample.transition.HorizontalCardStackTransition
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.scopedService
import de.gaw.kruiser.state.preview.PreviewNavigationState
import de.gaw.kruiser.transition.LocalExitTransitionTracker
import de.gaw.kruiser.transition.PreviewExitTransitionTracker
import kotlinx.coroutines.flow.update

object FormOneDestination : FormDestination {
    override fun build(): Screen = object : Screen {
        override val destination: Destination
            get() = this@FormOneDestination

        @Composable
        override fun Content() = HorizontalCardStackTransition {
            val sharedForm = scopedService(
                factory = SharedFormModelFactory,
                scope = SharedFormScope,
            )
            val model = scopedService(factory = FormOneModelFactory(sharedForm))
            FormOne(model = model)
        }
    }
}

@Composable
private fun FormOne(
    model: FormOneModel,
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Please Enter your name:")
                val name by model.name.collectAsState()
                OutlinedTextField(
                    value = name,
                    onValueChange = { text -> model.name.update { text } },
                )
                Text("Please Enter your nickname:")
                val nickname by model.nickname.collectAsState()
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { text -> model.nickname.update { text } },
                )
            }
            FormControlsPlaceholder()
        }
    }
}

@Preview
@Composable
private fun FormOnePreview() = MaterialTheme {
    CompositionLocalProvider(
        LocalNavigationState provides PreviewNavigationState(),
        LocalExitTransitionTracker provides PreviewExitTransitionTracker(),
    ) {
        FormOne(model = FormOneModel(SharedFormModel()))
    }
}


