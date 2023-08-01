package de.gaw.kruiser.sample.samples.wizard.one

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
    var name = sharedFormModel.name

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