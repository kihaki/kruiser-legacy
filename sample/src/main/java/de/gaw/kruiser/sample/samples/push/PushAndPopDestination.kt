package de.gaw.kruiser.sample.samples.push

import de.gaw.kruiser.destination.Destination

interface PushAndPopDestination : Destination {
    val index: Int
}

data class PushAndPopDestinationDefault(
    override val index: Int,
) : PushAndPopDestination {

    override fun build() = PushAndPopScreenHorizontal(
        index = index,
        destination = this,
    )
}
