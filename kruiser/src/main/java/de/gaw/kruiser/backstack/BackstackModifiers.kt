package de.gaw.kruiser.backstack

import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.MutableBackstackState
import de.gaw.kruiser.destination.Destination

fun MutableBackstackState.pop() = mutate { dropLast(1) }
fun MutableBackstackState.push(destination: Destination) = mutate { this + destination }

operator fun MutableBackstackState.plusAssign(destination: Destination) = push(destination)

operator fun BackstackEntries.plus(destination: Destination) =
    this + BackstackEntry(destination = destination)