package de.gaw.kruiser.sample.samples.push

import de.gaw.kruiser.sample.samples.bottomsheet.BottomSheetMenuDestination
import de.gaw.kruiser.sample.samples.wizard.one.FormOneDestination
import de.gaw.kruiser.screen.ScreenModel
import de.gaw.kruiser.service.ScopedServiceProvider
import de.gaw.kruiser.state.NavigationState
import de.gaw.kruiser.state.push

internal class PushAndPopScreenModel(
    private val index: Int,
    private val navigationState: NavigationState,
) : ScreenModel {
    fun onPushDefault() {
        navigationState.push(PushAndPopDestinationDefault(index + 1))
    }

    fun onShowBottomSheetDestination() {
        navigationState.push(BottomSheetMenuDestination("Menu ${index + 1}"))
    }

    fun onPushForm() {
        navigationState.push(FormOneDestination)
    }

    fun onGoToFirst() {
        navigationState.mutate { take(1) }
    }
}

internal data class PushAndPopScreenModelFactory(
    val index: Int,
) : ScopedServiceProvider.ServiceFactory<PushAndPopScreenModel> {
    override fun ScopedServiceProvider.ServiceContext.create(): PushAndPopScreenModel =
        PushAndPopScreenModel(index, navigationState)
}