package de.gaw.kruiser.sample.samples.wizard.shared

import android.util.Log
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

object SharedFormModelFactory : ServiceFactory<SharedFormModel> {
    override fun ServiceContext.create(): SharedFormModel = SharedFormModel()
}

/**
 * Model that will be shared between all form pages.
 */
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