package de.gaw.kruiser.sample.samples.wizard.two

import android.util.Log
import de.gaw.kruiser.sample.samples.wizard.shared.SharedFormModel
import de.gaw.kruiser.screen.ScreenModel
import de.gaw.kruiser.screen.scope
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceContext
import de.gaw.kruiser.service.ScopedServiceProvider.ServiceFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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
    val name = sharedFormModel.name

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