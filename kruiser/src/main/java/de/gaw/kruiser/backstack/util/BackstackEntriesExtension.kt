package de.gaw.kruiser.backstack.util

import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.destination.Destination

fun BackstackEntries.filterDestinations(predicate: (Destination) -> Boolean) =
    filter { predicate(it.destination) }