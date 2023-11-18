package de.gaw.kruiser.backstack

import de.gaw.kruiser.destination.Destination


fun MutableBackstack.pop() = mutate { dropLast(1) }

fun MutableBackstack.push(destination: Destination) = mutate { this + destination }

fun MutableBackstack.pushAvoidDuplicates(destination: Destination) = mutate {
    val currentItem = lastOrNull()
    when {
        currentItem != destination -> this + destination
        else -> this
    }
}