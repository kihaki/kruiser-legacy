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
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormModel
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormModelFactory
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormScope
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.service.scopedService
import de.gaw.kruiser.state.preview.PreviewNavigationState
import de.gaw.kruiser.ui.singletopstack.transition.LocalEntryExitTransitionStateTracker
import de.gaw.kruiser.ui.singletopstack.transition.PreviewEntryExitTransitionTracker
import kotlinx.coroutines.flow.update

object FormOneDestination : Destination {
    override fun build(): Screen = FormOneScreen()
}

private class FormOneScreen : Screen {
    override val destination: Destination = FormOneDestination

    @Composable
    override fun Content() {
        val sharedForm = scopedService(
            factory = SharedFormModelFactory,
            scope = SharedFormScope,
        )
        val model = scopedService(factory = FormOneModelFactory(sharedForm))
        FormOne(model = model)
    }
}

@Composable
private fun FormOne(model: FormOneModel) {
    val name by model.name.collectAsState()
    val nickname by model.nickname.collectAsState()
    FormOne(
        name = name,
        onNameChange = { text -> model.name.update { text } },
        nickname = nickname,
        onNicknameChange = { text -> model.nickname.update { text } },
    )
}

@Composable
private fun FormOne(
    name: String,
    onNameChange: (String) -> Unit,
    nickname: String,
    onNicknameChange: (String) -> Unit,
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
                Text("Please Enter your name:")
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                )
                Text("Please Enter your nickname:")
                OutlinedTextField(
                    value = nickname,
                    onValueChange = onNicknameChange,
                )
            }
        }
    }
}

@Preview
@Composable
private fun FormOnePreview() = MaterialTheme {
    CompositionLocalProvider(
        LocalNavigationState provides PreviewNavigationState(),
        LocalEntryExitTransitionStateTracker provides PreviewEntryExitTransitionTracker(),
    ) {
        FormOne(model = FormOneModel(SharedFormModel()))
    }
}


