package de.gaw.kruiser.backstack

import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.MutableBackstack
import de.gaw.kruiser.destination.Destination

fun MutableBackstack.pop() = mutate { dropLast(1) }

fun MutableBackstack.push(destination: Destination) = mutate { this + destination }

fun MutableBackstack.pushAvoidDuplicate(destination: Destination) = mutate {
    val currentItem = lastOrNull()
    when {
        currentItem?.destination != destination -> this + destination
        else -> this
    }
}

operator fun BackstackEntries.plus(destination: Destination) =
    this + BackstackEntry(destination = destination)