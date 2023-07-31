package de.gaw.kruiser.sample.samples.push

import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.sample.transition.VerticalCardStackTransition
import de.gaw.kruiser.screen.ScreenTransition

interface PushAndPopDestination : Destination {
    val index: Int
}

data class PushAndPopDestinationDefault(
    override val index: Int,
) : PushAndPopDestination {

    override fun build() = PushAndPopScreen(
        index = index,
        destination = this,
    )
}

data class PushAndPopDestinationVertical(
    override val index: Int,
) : PushAndPopDestination,
    ScreenTransition by VerticalCardStackTransition() {

    override fun build() = PushAndPopScreen(
        index = index,
        destination = this,
    )
}
