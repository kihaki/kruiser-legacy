package de.gaw.kruiser.sample.samples.wizard

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.gaw.kruiser.android.LocalNavigationState
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.remoteui.RemoteUi
import de.gaw.kruiser.remoteui.RemoteUiPlaceholder
import de.gaw.kruiser.sample.transition.HorizontalCardStackTransition
import de.gaw.kruiser.screen.Screen
import de.gaw.kruiser.screen.ScreenModel
import de.gaw.kruiser.screen.scope
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceContext
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceFactory
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceScope
import de.gaw.kruiser.service.scopedService
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.collectCurrentStack
import de.gaw.kruiser.state.currentStack
import de.gaw.kruiser.state.pop
import de.gaw.kruiser.state.push
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

object FormOneDestination : FormDestination {
    override fun build(): Screen = object : Screen {
        override val destination: Destination
            get() = this@FormOneDestination

        @Composable
        override fun Content() = HorizontalCardStackTransition {
            Surface {
                val sharedForm = scopedService(
                    factory = SharedFormModelFactory,
                    scope = AllFormsScope,
                )
                val formOne = scopedService(factory = FormOneModelFactory(sharedForm))
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
                        val name by sharedForm.name.collectAsState()
                        OutlinedTextField(
                            value = name,
                            onValueChange = { text -> sharedForm.name.update { text } },
                        )
                        Text("Please Enter your nickname:")
                        val nickname by formOne.nickname.collectAsState()
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = { text -> formOne.nickname.update { text } },
                        )
                    }
                    FormControlsPlaceholder()
                }
            }
        }
    }
}

const val KEY_FORM_CONTROLS = "key-form-controls"

@Composable
fun FormControls() {
    val navigationState = LocalNavigationState.current
    val stack by navigationState.collectCurrentStack()
    val zIndex by remember(navigationState) {
        derivedStateOf {
            stack.indexOfLast { it is FormDestination }.toFloat()
        }
    }
    RemoteUi(
        key = KEY_FORM_CONTROLS,
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
                    onClick = { navigationState.push(FormTwoDestination) }
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
fun FormControlsPlaceholder() =
    RemoteUiPlaceholder(key = KEY_FORM_CONTROLS) { stack: List<Destination> ->
        stack.previousDestination() !is FormDestination
    }

fun List<Destination>.previousDestination() = dropLast(1).lastOrNull()

object FormTwoDestination : FormDestination {
    override fun build(): Screen = object : Screen {
        override val destination: Destination
            get() = this@FormTwoDestination

        @Composable
        override fun Content() = HorizontalCardStackTransition {
            Surface {
                val sharedForm = scopedService(
                    factory = SharedFormModelFactory,
                    scope = AllFormsScope,
                )
                val formTwo = scopedService(factory = FormTwoModelFactory(sharedForm))
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
                        val name by sharedForm.name.collectAsState()
                        Text("Your selected name is $name, please enter your address:")
                        val address by formTwo.address.collectAsState()
                        OutlinedTextField(
                            value = address,
                            onValueChange = { text -> formTwo.address.update { text } },
                        )
                    }
                    FormControlsPlaceholder()
                }
            }
        }
    }
}

interface FormDestination : Destination

object AllFormsScope : ServiceScope {
    override fun isAlive(state: NavigationState): Boolean = state.currentStack.any { destination ->
        destination is FormDestination
    }
}

data class FormOneModelFactory(
    val sharedFormModel: SharedFormModel,
) : ServiceFactory<FormOneModel> {
    override fun ServiceContext.create(): FormOneModel =
        FormOneModel(sharedFormModel = sharedFormModel)
}

@OptIn(FlowPreview::class)
class FormOneModel(
    val sharedFormModel: SharedFormModel,
) : ScreenModel {
    var nickname = MutableStateFlow("")

    init {
        scope.launch {
            sharedFormModel.name
                .debounce(500.milliseconds)
                .collectLatest {
                    Log.v("Form", "Form 01: Name in shared form changed to $it")
                }
        }
        scope.launch {
            nickname
                .debounce(500.milliseconds)
                .collectLatest {
                    Log.v("Form", "Form 01: Nickname changed to $it")
                }
        }
    }
}

data class FormTwoModelFactory(
    val sharedFormModel: SharedFormModel,
) : ServiceFactory<FormTwoModel> {
    override fun ServiceContext.create(): FormTwoModel =
        FormTwoModel(sharedFormModel = sharedFormModel)
}

@OptIn(FlowPreview::class)
class FormTwoModel(
    val sharedFormModel: SharedFormModel,
) : ScreenModel {
    val address = MutableStateFlow("")

    init {
        scope.launch {
            sharedFormModel.name
                .debounce(500.milliseconds)
                .collectLatest {
                    Log.v("Form", "Form 02: Name in shared form changed to $it")
                }
        }
        scope.launch {
            address
                .debounce(500.milliseconds)
                .collectLatest {
                    Log.v("Form", "Form 02: Address changed to $it")
                }
        }
    }
}

object SharedFormModelFactory : ServiceFactory<SharedFormModel> {
    override fun ServiceContext.create(): SharedFormModel = SharedFormModel()
}

@OptIn(FlowPreview::class)
class SharedFormModel : ScreenModel {
    val name = MutableStateFlow("")

    init {
        scope.launch {
            name
                .debounce(500.milliseconds)
                .collectLatest {
                    Log.v("Form", "Form Shared: Name changed to $it")
                }
        }
    }
}