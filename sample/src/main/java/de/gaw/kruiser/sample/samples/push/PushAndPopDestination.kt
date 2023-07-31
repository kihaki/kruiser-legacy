package de.gaw.kruiser.sample.samples.push

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.sample.transition.VerticalCardStackTransition
import de.gaw.kruiser.screen.ScreenTransition

data class PushAndPopDestinationDefault(
    val index: Int,
) : Destination {

    override fun build() = PushAndPopScreen(
        index = index,
        destination = this,
    )
}

data class PushAndPopDestinationVertical(
    val index: Int,
) : Destination,
    ScreenTransition by VerticalCardStackTransition() {

    override fun build() = PushAndPopScreen(
        index = index,
        destination = this,
    )
}
