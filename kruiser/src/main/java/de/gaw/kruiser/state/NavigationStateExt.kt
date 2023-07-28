package de.gaw.kruiser.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.destination.Destination

fun NavigationState.push(destination: Destination) = mutate { add(destination) }
fun NavigationState.pop() = mutate { removeLastOrNull() }
fun NavigationState.isEmpty() = currentStack.isEmpty()
val NavigationState.currentLastEvent get() = lastEvent.value
val NavigationState.currentStack get() = stack.value
val NavigationState.currentDestination get() = currentStack.lastOrNull()

@Composable
fun NavigationState.rememberIsEmpty(): State<Boolean> {
    val stack by stack.collectAsState()
    return remember(this.stack) { derivedStateOf { stack.isEmpty() } }
}

@Composable
fun NavigationState.rememberCurrentDestination(): State<Destination?> {
    val stack by stack.collectAsState()
    return remember(this.stack) { derivedStateOf { stack.lastOrNull() } }
}