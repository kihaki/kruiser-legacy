package de.gaw.kruiser.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.destination.Destination
import de.gaw.kruiser.state.NavigationState.Event

fun NavigationState.push(destination: Destination) = mutate { this + destination }
fun NavigationState.pop() = mutate { dropLast(1) }
fun NavigationState.popUntil(block: (Destination) -> Boolean) =
    mutate { dropLastWhile { !block(it) } }

fun NavigationState.isEmpty() = currentStack.isEmpty()
val NavigationState.currentLastEvent get() = lastEvent.value
val NavigationState.currentStack get() = stack.value
val NavigationState.currentDestination get() = currentStack.lastOrNull()

@Composable
fun NavigationState.collectCurrentStack(): State<List<Destination>> =
    stack.collectAsState()

@Composable
fun NavigationState.collectCurrentEvent(): State<Event> =
    lastEvent.collectAsState()

@Composable
fun NavigationState.collectCurrentDestination(): State<Destination?> {
    val stack by collectCurrentStack()
    return remember(this.stack) { derivedStateOf { stack.lastOrNull() } }
}

@Composable
fun NavigationState.collectIsEmpty(): State<Boolean> {
    val stack by collectCurrentStack()
    return remember(this.stack) { derivedStateOf { stack.isEmpty() } }
}
